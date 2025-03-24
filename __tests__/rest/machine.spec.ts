import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import errorCodes from "../../src/constants/textCodes";
import endpoints from "../../src/constants/endpoints";
import user from "../../src/service/user";
import site from "../../src/service/site";
import { Rol } from "@prisma/client";
import jwtUse from "../../src/core/jwtUse";
import resetDatabase from "../../src/data/resetDatabase";

// TEST DATA
let machineEndpoint = endpoints.apiPrefix + endpoints.machinePrefix;

// TESTS
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

describe("/machine", () => {
    let adminToken: string;
    let adminId: number;
    let technicianToken: string;
    let technicianId: number;
    let managerToken: string;
    let managerId: number;
    let userToken: string;
    let userId: number;
    let siteId: number;
    let machineId: number;

    beforeEach(async () => {
        // Create admin user
        adminToken = await user.create({
            voornaam: "Admin",
            achternaam: "User",
            email: "admin@example.com",
            password: "Password123",
            straat: "Admin Street",
            huis_nr: "1",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Admin City",
            postcode: "12345",
            land: "Test Country",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.ADMINISTRATOR,
        });
        adminId = jwtUse.getUserID(adminToken);

        // Create technician user
        technicianToken = await user.create({
            voornaam: "Tech",
            achternaam: "User",
            email: "tech@example.com",
            password: "Password123",
            straat: "Tech Street",
            huis_nr: "2",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Tech City",
            postcode: "12345",
            land: "Test Country",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.TECHNIEKER,
        });
        technicianId = jwtUse.getUserID(technicianToken);

        // Create manager user
        managerToken = await user.create({
            voornaam: "Manager",
            achternaam: "User",
            email: "manager@example.com",
            password: "Password123",
            straat: "Manager Street",
            huis_nr: "3",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Manager City",
            postcode: "12345",
            land: "Test Country",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.MANAGER,
        });
        managerId = jwtUse.getUserID(managerToken);

        // Create regular user
        userToken = await user.create({
            voornaam: "Regular",
            achternaam: "User",
            email: "user@example.com",
            password: "Password123",
            straat: "User Street",
            huis_nr: "4",
            geboorteDatum: new Date("1990-01-01"),
            stad: "User City",
            postcode: "12345",
            land: "Test Country",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.GEBRUIKER,
        });
        userId = jwtUse.getUserID(userToken);

        // Create a site manager
        const verantwToken = await user.create({
            voornaam: "Verantw",
            achternaam: "User",
            email: "verantw@example.com",
            password: "Password123",
            straat: "Verantw Street",
            huis_nr: "5",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Verantw City",
            postcode: "12345",
            land: "Test Country",
            gsm_nr: "123456789",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });
        const verantwId = jwtUse.getUserID(verantwToken);

        // Create a site
        siteId = await site.create({
            naam: "Test Site",
            verantw_id: verantwId,
        });
    });

    it("POST - Admin can create a machine", async () => {
        const response = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Test Location",
                info: "Test Machine Info",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        expect(response.status).toBe(200);
        expect(response.body.id).toBeDefined();
        machineId = response.body.id; // Save for later tests
    });

    it("POST - Technician can create a machine", async () => {
        const response = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${technicianToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Tech Location",
                info: "Technician Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 95,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 5,
                volgendOnderhoud: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString(),
            });

        expect(response.status).toBe(200);
        expect(response.body.id).toBeDefined();
    });

    it("POST - Regular user cannot create a machine", async () => {
        const response = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${userToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "User Location",
                info: "User Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 90,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 10,
                volgendOnderhoud: new Date(Date.now() + 20 * 24 * 60 * 60 * 1000).toISOString(),
            });

        expect(response.status).toBe(401);
        expect(response.body.message).toBe(errorCodes.NOACCESS);
    });

    it("GET - Admin can get all machines", async () => {
        // First create a machine
        const createResponse = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Test Location",
                info: "Test Machine Info",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        machineId = createResponse.body.id;

        const response = await request.get(machineEndpoint).set({
            Authorization: `Bearer ${adminToken}`,
        });

        expect(response.status).toBe(200);
        expect(Array.isArray(response.body)).toBe(true);
        expect(response.body.length).toBeGreaterThan(0);
    });

    it("GET - Regular user cannot get machines", async () => {
        const response = await request.get(machineEndpoint).set({
            Authorization: `Bearer ${userToken}`,
        });

        expect(response.status).toBe(401);
        expect(response.body.message).toBe(errorCodes.NOACCESS);
    });

    it("GET/:id - Admin can get a specific machine", async () => {
        // First create a machine
        const createResponse = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Test Location",
                info: "Test Machine Info",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        machineId = createResponse.body.id;

        const response = await request.get(`${machineEndpoint}/${machineId}`).set({
            Authorization: `Bearer ${adminToken}`,
        });

        expect(response.status).toBe(200);
        expect(response.body.id).toBe(machineId);
    });

    it("PUT - Admin can update a machine", async () => {
        // First create a machine
        const createResponse = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Original Location",
                info: "Original Info",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        machineId = createResponse.body.id;

        const response = await request
            .put(`${machineEndpoint}/${machineId}`)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                locatie: "Updated Location",
                status: "Maintenance",
            });

        expect(response.status).toBe(200);
        expect(response.body.locatie).toBe("Updated Location");
        expect(response.body.status).toBe("Maintenance");
    });

    it("DELETE - Admin can delete a machine", async () => {
        // First create a machine
        const createResponse = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Location to Delete",
                info: "Machine to Delete",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        machineId = createResponse.body.id;

        const response = await request.delete(`${machineEndpoint}/${machineId}`).set({
            Authorization: `Bearer ${adminToken}`,
        });

        expect(response.status).toBe(200);

        // Verify it's deleted
        const getResponse = await request.get(`${machineEndpoint}/${machineId}`).set({
            Authorization: `Bearer ${adminToken}`,
        });

        expect(getResponse.status).toBe(404);
    });

    it("DELETE - Regular user cannot delete a machine", async () => {
        // First create a machine
        const createResponse = await request
            .post(machineEndpoint)
            .set({
                Authorization: `Bearer ${adminToken}`,
            })
            .send({
                site_id: siteId,
                locatie: "Location for User Test",
                info: "Machine for User Test",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: technicianId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            });

        machineId = createResponse.body.id;

        const response = await request.delete(`${machineEndpoint}/${machineId}`).set({
            Authorization: `Bearer ${userToken}`,
        });

        expect(response.status).toBe(401);
        expect(response.body.message).toBe(errorCodes.NOACCESS);
    });
});
