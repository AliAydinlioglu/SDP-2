import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import jwtUse from "../../src/core/jwtUse";
import userRepository from "../../src/repository/user";
import userService from "../../src/service/user";
import textCodes from "../../src/constants/textCodes";

const IDFromResponse = (response: supertest.Response) => {
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
    await userRepository.deleteItems("id", id);

    for (let id of ids) {
        await userRepository.deleteItems("id", id);
    }
});

describe("/api/auth/register", () => {
    it("GET - Register twice - should 200 and create a user in the DB.", async () => {
        // Will try to access the user endpoint with a session_id
        const response = await request.post("/api/auth/register").send({
            name: "Vincent",
            email: "vincent@example.com",
            password: "12345678",
        });

        id = IDFromResponse(response);
        expect(response.status).toBe(200);
        expect(userService.find(id)).not.toBeNull();
    });
    it("GET - Don't register due to the password being too short", async () => {
        // Will try to access the user endpoint with a session_id
        const response = await request.post("/api/auth/register").send({
            name: "Vincent",
            email: "vincent@example.com",
            password: "1234567",
        });

        expect(response.status).toBe(400);
        expect(response.body.message).toEqual(textCodes.SHORTPASSWORD);
    });
    it("GET - Register twice -  Attempt to register twice with the same email", async () => {
        let response = await request.post("/api/auth/register").send({
            name: "Vincent 1",
            email: "vincent@example.com",
            password: "12345678",
        });

        let id1 = IDFromResponse(response);
        ids.push(id1);

        let errorResponse = await request.post("/api/auth/register").send({
            name: "Vincent 2",
            email: "vincent@example.com",
            password: "12345678",
        });

        expect(errorResponse.status).toBe(400);
        expect(errorResponse.body.message).toEqual(textCodes.DUPLICATE);
    });

    it("GET - Attempt to register twice with the same email but different character capitalization", async () => {
        let response = await request.post("/api/auth/register").send({
            name: "Vincent 1",
            email: "vincent@example.com",
            password: "12345678",
        });

        let id1 = IDFromResponse(response);
        ids.push(id1);

        let errorResponse = await request.post("/api/auth/register").send({
            name: "Vincent 2",
            email: "VINCENT@example.com",
            password: "12345678",
        });

        expect(errorResponse.status).toBe(400);
        expect(errorResponse.body.message).toEqual(textCodes.DUPLICATE);
    });
});

describe("/api/auth/login", () => {
    it("GET - Should 200 and login to a user in the DB.", async () => {
        await request.post("/api/auth/register").send({
            name: "Valerie",
            email: "Valerie@example.com",
            password: "54321",
        });

        // Create a user first
        const jwtResponse = await request.post("/api/auth/login").send({
            email: "Valerie@example.com",
            password: "54321",
        });

        expect(jwtResponse.status).toBe(200);
        id = IDFromResponse(jwtResponse);
        expect(userService.find(id)).not.toBeNull();
    });
});
