import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import endpoints from "../../src/constants/endpoints";
import userService from "../../src/service/user";
import siteService from "../../src/service/site";
import machineService from "../../src/service/machine";
import jwtUse from "../../src/core/jwtUse";
import resetDatabase from "../../src/data/resetDatabase";
import data from "../../src/data";
import { Rol } from "@prisma/client";
import errorCodes from "../../src/constants/textCodes";

// TEST ENDPOINT
const onderhoudEndpoint = endpoints.apiPrefix + endpoints.onderhoudPrefix;

let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

beforeEach(async () => {
    await resetDatabase();
});

afterAll(async () => {
    await server.stop();
});

describe("/onderhoud", () => {
    let adminToken: string;
    let technicianToken: string;
    let managerToken: string;
    let userToken: string;
    let adminId: number;
    let technicianId: number;
    let managerId: number;
    let userId: number;
    let siteId: number;
    let machineId: number;

    beforeEach(async () => {
        // Create admin user
        adminToken = await userService.create({
            voornaam: "Admin",
            achternaam: "User",
            email: "admin@example.com",
            password: "Password123",
            straat: "Admin Street",
            huis_nr: "1",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Admin City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.ADMINISTRATOR,
        });
        adminId = jwtUse.getUserID(adminToken);

        // Create technician user
        technicianToken = await userService.create({
            voornaam: "Tech",
            achternaam: "User",
            email: "tech@example.com",
            password: "Password123",
            straat: "Tech Street",
            huis_nr: "2",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Tech City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.TECHNIEKER,
        });
        technicianId = jwtUse.getUserID(technicianToken);

        // Create manager user
        managerToken = await userService.create({
            voornaam: "Manager",
            achternaam: "User",
            email: "manager@example.com",
            password: "Password123",
            straat: "Manager Street",
            huis_nr: "3",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Manager City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.MANAGER,
        });
        managerId = jwtUse.getUserID(managerToken);

        // Create regular user
        userToken = await userService.create({
            voornaam: "Regular",
            achternaam: "User",
            email: "user@example.com",
            password: "Password123",
            straat: "User Street",
            huis_nr: "4",
            geboorteDatum: new Date("1990-01-01"),
            stad: "User City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.GEBRUIKER,
        });
        userId = jwtUse.getUserID(userToken);

        // Create a site manager (verantwoordelijke)
        const verantwToken = await userService.create({
            voornaam: "Verantw",
            achternaam: "User",
            email: "verantw@example.com",
            password: "Password123",
            straat: "Verantw Street",
            huis_nr: "5",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Verantw City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });
        const verantwId = jwtUse.getUserID(verantwToken);

        // Create a site
        siteId = await siteService.create({
            naam: "Test Site",
            verantw_id: verantwId,
        });

        // Create a machine (required for maintenance)
        const machineData = {
            site_id: siteId,
            locatie: "Machine Location",
            info: "Test Machine",
            status: "Operational",
            prod_status: "Active",
            uptime: 100,
            technieker_id: technicianId,
            dagenSindsOnderhoud: 0,
            volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
        };
        const machine = await machineService.create(machineData);
        machineId = machine.id;
    });

    describe("POST /onderhoud", () => {
        it("should allow admin to create an onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const response = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${adminToken}` })
                .send(onderhoudData);

            expect(response.status).toBe(200);
            expect(response.body.id).toBeDefined();
            expect(response.body.reden).toBe(onderhoudData.reden);
        });

        it("should not allow regular user to create an onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const response = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${userToken}` })
                .send(onderhoudData);

            expect(response.status).toBe(401);
            expect(response.body.message).toBe(errorCodes.NOACCESS);
        });
    });

    describe("GET /onderhoud", () => {
        it("should allow admin to get all onderhoud records", async () => {
            // Create two onderhoud records first
            const onderhoudData1 = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const onderhoudData2 = {
                datum: new Date("2025-03-26"),
                startTijd: new Date("2025-03-26T09:00:00.000Z"),
                eindTijd: new Date("2025-03-26T13:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Repair",
                rapport: { findings: "Minor issues found" },
                opmerkingen: "Follow-up needed",
                status: "InProgress",
                machine_id: machineId,
            };

            await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${managerToken}` })
                .send(onderhoudData1);
            await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${managerToken}` })
                .send(onderhoudData2);

            const response = await request.get(onderhoudEndpoint).set({ Authorization: `Bearer ${adminToken}` });
            expect(response.status).toBe(200);
            expect(Array.isArray(response.body)).toBe(true);
            expect(response.body.length).toBe(2);
        });

        it("should not allow regular user to get onderhoud records", async () => {
            const response = await request.get(onderhoudEndpoint).set({ Authorization: `Bearer ${userToken}` });
            expect(response.status).toBe(401);
            expect(response.body.message).toBe(errorCodes.NOACCESS);
        });
    });

    describe("GET /onderhoud/:id", () => {
        it("should allow admin to get a specific onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createResponse = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${adminToken}` })
                .send(onderhoudData);
            const onderhoudId = createResponse.body.id;

            const response = await request.get(`${onderhoudEndpoint}/${onderhoudId}`).set({ Authorization: `Bearer ${adminToken}` });
            expect(response.status).toBe(200);
            expect(response.body.id).toBe(onderhoudId);
        });

        it("should return 404 for non-existent onderhoud record", async () => {
            const response = await request.get(`${onderhoudEndpoint}/999999`).set({ Authorization: `Bearer ${adminToken}` });
            expect(response.status).toBe(404);
            expect(response.body.message).toBe(errorCodes.NOTFOUND);
        });
    });

    describe("PUT /onderhoud/:id", () => {
        it("should allow admin to update an onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "Operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createResponse = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${managerToken}` })
                .send(onderhoudData);
            const onderhoudId = createResponse.body.id;

            const updateData = {
                reden: "Emergency Fix",
                status: "Completed",
                rapport: { findings: "Fixed issues" },
            };

            const response = await request
                .put(`${onderhoudEndpoint}/${onderhoudId}`)
                .set({ Authorization: `Bearer ${adminToken}` })
                .send(updateData);

            expect(response.status).toBe(200);
            expect(response.body.reden).toBe(updateData.reden);
            expect(response.body.status).toBe(updateData.status);
            expect(response.body.rapport).toEqual(updateData.rapport);
        });

        it("should return 404 when updating non-existent onderhoud record", async () => {
            const updateData = { status: "Broken" };
            const response = await request
                .put(`${onderhoudEndpoint}/999999`)
                .set({ Authorization: `Bearer ${adminToken}` })
                .send(updateData);
            expect(response.status).toBe(404);
            expect(response.body.message).toBe(errorCodes.NOTFOUND);
        });
    });

    describe("DELETE /onderhoud/:id", () => {
        it("should allow admin to delete an onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "Operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createResponse = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${managerToken}` })
                .send(onderhoudData);
            const onderhoudId = createResponse.body.id;

            const deleteResponse = await request.delete(`${onderhoudEndpoint}/${onderhoudId}`).set({ Authorization: `Bearer ${adminToken}` });
            expect(deleteResponse.status).toBe(200);

            const getResponse = await request.get(`${onderhoudEndpoint}/${onderhoudId}`).set({ Authorization: `Bearer ${adminToken}` });
            expect(getResponse.status).toBe(404);
            expect(getResponse.body.message).toBe(errorCodes.NOTFOUND);
        });

        it("should not allow regular user to delete an onderhoud record", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: technicianId,
                reden: "Routine Check",
                rapport: { findings: "Operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createResponse = await request
                .post(onderhoudEndpoint)
                .set({ Authorization: `Bearer ${managerToken}` })
                .send(onderhoudData);
            const onderhoudId = createResponse.body.id;

            const response = await request.delete(`${onderhoudEndpoint}/${onderhoudId}`).set({ Authorization: `Bearer ${userToken}` });
            expect(response.status).toBe(401);
            expect(response.body.message).toBe(errorCodes.NOACCESS);
        });
    });
});
