import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import errorCodes from "../../src/constants/textCodes";
import testData from "../testdata";
import endpoints from "../../src/constants/endpoints";
import site from "../../src/service/site";
import user from "../../src/service/user";
import { Rol } from "@prisma/client";
import jwtUse from "../../src/core/jwtUse";
import data from "../../src/data";
import resetDatabase from "../../src/data/resetDatabase";
// TEST DATA

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

describe("/sites", () => {
    it("Should retrieve a site by ID", async () => {
        // Create a verantwoordelijke user
        let jwt = await user.create({
            voornaam: "John",
            achternaam: "Doe",
            email: "john.doe@email.com",
            password: "securepassword",
            straat: "Main Street",
            huis_nr: "10",
            geboorteDatum: new Date("1985-06-15"),
            stad: "Brussels",
            postcode: "1000",
            land: "Belgium",
            gsm_nr: "555-1234",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });

        let id = jwtUse.getUserID(jwt);

        let site_id = await site.create({ naam: "Test Site", verantw_id: id });

        let siteObject = await site.find(site_id);

        expect(siteObject!.naam).toBe("Test Site");
        expect(siteObject!.verantw_id).toBe(id);
    });

    it("Should not create a site, due to the user not being verantwoordelijke", async () => {
        // First create a user that's a regular GEBRUIKER
        let jwt = await user.create({
            voornaam: "Solomon",
            achternaam: "Reed",
            email: "solomonreede@email.com",
            password: "therealslimshady",
            straat: "Corpo Plaza",
            huis_nr: "505",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Night City",
            postcode: "77704",
            land: "NUSA",
            gsm_nr: "555-1701",
            actief: true,
            rol: Rol.GEBRUIKER, // Regular user, not VERANTWOORDELIJKE
        });

        let id = jwtUse.getUserID(jwt);

        try {
            await site.create({ naam: "site1", verantw_id: id });
            expect(true).toBe(false); // If no error is thrown, fail the test
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.INVALIDDATA);
        }
    });

    it("Should update a site", async () => {
        let jwt = await user.create({
            voornaam: "Alice",
            achternaam: "Smith",
            email: "alice.smith@email.com",
            password: "anotherpassword",
            straat: "Second Avenue",
            huis_nr: "20",
            geboorteDatum: new Date("1990-02-20"),
            stad: "Antwerp",
            postcode: "2000",
            land: "Belgium",
            gsm_nr: "555-5678",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });

        let id = jwtUse.getUserID(jwt);

        let site_id = await site.create({ naam: "Old Site Name", verantw_id: id });

        await site.update(site_id, { naam: "New Site Name" });

        let siteObject = await site.find(site_id);
        expect(siteObject!.naam).toBe("New Site Name");
    });

    it("Should not update a site with invalid data", async () => {
        let jwt = await user.create({
            voornaam: "Bob",
            achternaam: "Marley",
            email: "bob.marley@email.com",
            password: "legend123",
            straat: "Reggae Road",
            huis_nr: "77",
            geboorteDatum: new Date("1945-02-06"),
            stad: "Kingston",
            postcode: "87600",
            land: "Jamaica",
            gsm_nr: "555-7777",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });

        let id = jwtUse.getUserID(jwt);

        let site_id = await site.create({ naam: "Original Site", verantw_id: id });

        try {
            await site.update(site_id, { naam: "" }); // Invalid name
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.INVALIDDATA);
        }
    });

    it("Should delete a site", async () => {
        let jwt = await user.create({
            voornaam: "Charlie",
            achternaam: "Brown",
            email: "charlie.brown@email.com",
            password: "secure1234",
            straat: "Third Street",
            huis_nr: "30",
            geboorteDatum: new Date("1988-09-10"),
            stad: "Ghent",
            postcode: "9000",
            land: "Belgium",
            gsm_nr: "555-9876",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });

        let id = jwtUse.getUserID(jwt);

        let site_id = await site.create({ naam: "Temporary Site", verantw_id: id });

        await site.deleteSite(site_id);

        try {
            await site.find(site_id);
            expect(true).toBe(false); 
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.SITENOTFOUND);
        }
    });


    it("Should not delete a non-existing site", async () => {
        let jwt = await user.create({
            voornaam: "Eve",
            achternaam: "Johnson",
            email: "eve.johnson@email.com",
            password: "mypassword123",
            straat: "Fifth Avenue",
            huis_nr: "40",
            geboorteDatum: new Date("1992-11-25"),
            stad: "Bruges",
            postcode: "8000",
            land: "Belgium",
            gsm_nr: "555-1111",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });

        try {
            await site.deleteSite(99999); // Non-existent site ID
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.SITENOTFOUND);
        }
    });
});
