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

describe("/users", () => {
    it("Should create a site", async () => {
        // first create a user that's a verantwoordelijke
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
            rol: Rol.VERANTWOORDELIJKE,
        });

        let id = jwtUse.getUserID(jwt);

        let site_id = await site.create({ naam: "site1", verantw_id: id });

        let siteObject = await site.find(site_id);

        expect(siteObject!.naam).toBe("site1");
        expect(siteObject!.verantw_id).toBe(id);
    });
    it("Should not create a site, due to the user not being verantwoordelijke", async () => {
        // first create a user that's a verantwoordelijke
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
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);

        try {
            await site.create({ naam: "site1", verantw_id: id });
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.INVALIDDATA);
        }
    });
});
