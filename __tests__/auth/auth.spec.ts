import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import jwtUse from "../../src/core/jwtUse";
import userService from "../../src/service/user";
import textCodes from "../../src/constants/textCodes";
import { Rol } from "@prisma/client";
import data from "../../src/data";

const IDFromResponse = (response: supertest.Response): number => {
    return jwtUse.getUserID(response.headers.authorization.split(" ")[1]);
};

// TEST

let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

afterAll(async () => {
    await server.stop();
});

let id: number;
let ids: number[] = [];

beforeEach(async () => {
    id = -1; // making sure it's not set to a valid id before each test.
});

afterEach(async () => {
    await data.prisma.user.deleteMany();
});

describe("/api/auth/register", () => {
    it("GET - Register - should 200 and create a user in the DB.", async () => {
        // Will try to access the user endpoint with a session_id
        const response = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "Legend",
            email: "vincent@example.com",
            password: "12345678",
            straat: "Watson Avenue",
            huis_nr: "221",
            stad: "Night City",
            postcode: "77704",
            land: "NUSA",
            gsm_nr: "555-7766",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.ADMINISTRATOR,
        });

        id = IDFromResponse(response);
        expect(response.status).toBe(200);
        expect(userService.find(id)).not.toBeNull();
    });
    it("GET - Don't register due to the password being too short", async () => {
        // Will try to access the user endpoint with a session_id
        const response = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "Deraad",
            email: "vincent@example.com",
            password: "1234567",
            straat: "Kabuki Street",
            huis_nr: "42",
            stad: "Night City",
            postcode: "77705",
            land: "NUSA",
            gsm_nr: "555-1234",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.TECHNIEKER,
        });

        expect(response.status).toBe(400);
        expect(response.body.message).toEqual(textCodes.SHORTPASSWORD);
    });
    it("GET - Register twice -  Attempt to register twice with the same email", async () => {
        let response = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "First",
            email: "vincent@example.com",
            password: "12345678",
            straat: "Jig-Jig Street",
            huis_nr: "101",
            stad: "Night City",
            postcode: "77709",
            land: "NUSA",
            gsm_nr: "555-8001",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.TECHNIEKER,
        });

        let id1 = IDFromResponse(response);
        ids.push(id1);

        let errorResponse = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "Second",
            email: "vincent@example.com",
            password: "12345678",
            straat: "Pacifica Boulevard",
            huis_nr: "202",
            stad: "Night City",
            postcode: "77709",
            land: "NUSA",
            gsm_nr: "555-8002",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.TECHNIEKER,
        });

        expect(errorResponse.status).toBe(400);
        expect(errorResponse.body.message).toEqual(textCodes.DUPLICATE);
    });

    it("GET - Attempt to register twice with the same email but different character capitalization", async () => {
        let response = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "Lower",
            email: "vincent@example.com",
            password: "12345678",
            straat: "Heywood Plaza",
            huis_nr: "303",
            stad: "Night City",
            postcode: "77708",
            land: "NUSA",
            gsm_nr: "555-9001",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        let id1 = IDFromResponse(response);
        ids.push(id1);

        let errorResponse = await request.post("/api/auth/register").send({
            voornaam: "Vincent",
            achternaam: "Upper",
            email: "VINCENT@example.com",
            password: "12345678",
            straat: "Westbrook Heights",
            huis_nr: "404",
            stad: "Night City",
            postcode: "77707",
            land: "NUSA",
            gsm_nr: "555-9002",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        expect(errorResponse.status).toBe(400);
        expect(errorResponse.body.message).toEqual(textCodes.DUPLICATE);
    });
});

describe("/api/auth/login", () => {
    it("GET - Should 200 and login to a user in the DB.", async () => {
        // Will try to access the user endpoint with a session_id
        const response = await request.post("/api/auth/register").send({
            voornaam: "Valerie",
            achternaam: "Chrome",
            email: "Valerie@example.com",
            password: "12345678910",
            straat: "Arasaka Tower",
            huis_nr: "777",
            stad: "Night City",
            postcode: "77701",
            land: "NUSA",
            gsm_nr: "555-3030",
            geboorteDatum: new Date("1990-01-02"),
            actief: true,
            rol: Rol.TECHNIEKER,
        });

        id = IDFromResponse(response);
        expect(response.status).toBe(200);
        expect(userService.find(id)).not.toBeNull();

        const jwtResponse = await request.post("/api/auth/login").send({
            email: "Valerie@example.com",
            password: "12345678910",
        });

        expect(jwtResponse.status).toBe(200);
        id = IDFromResponse(jwtResponse);
        expect(userService.find(id)).not.toBeNull();
    });
});
