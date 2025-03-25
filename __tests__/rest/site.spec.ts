import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import errorCodes from "../../src/constants/textCodes";
import endpoints from "../../src/constants/endpoints";
import site from "../../src/service/site";
import user from "../../src/service/user";
import { Rol } from "@prisma/client";
import jwtUse from "../../src/core/jwtUse";
import data from "../../src/data";
import resetDatabase from "../../src/data/resetDatabase";
// TEST DATA

let siteEndpoint = endpoints.apiPrefix + endpoints.sitePrefix;

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

describe("/site", () => {
    it("GET - Admin gets all sites", async () => {
        let verantwJWT = await user.create({
            voornaam: "Solomon",
            achternaam: "Reed",
            email: "verantwoordelijke@email.com",
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

        let verantw_id = jwtUse.getUserID(verantwJWT);

        let site1 = await site.create({
            naam: "Site 1",
            verantw_id: verantw_id,
        });

        let site2 = await site.create({
            naam: "Site 2",
            verantw_id: verantw_id,
        });

        let adminJWT = await user.create({
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
            rol: Rol.ADMINISTRATOR,
        });

        const response = await request.get(siteEndpoint).set({
            Authorization: `Bearer ${adminJWT}`,
        });

        expect(response.status).toBe(200);
        expect(response.body.length).toBe(2);

        expect(response.body[0].naam).toBe("Site 1");
        expect(response.body[0].verantw_id).toBe(verantw_id);
    });

    it("POST - Admin assignes verantwoordelijke to a site", async () => {
        // first create a user that's a verantwoordelijke
        let adminJWT = await user.create({
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
            rol: Rol.ADMINISTRATOR,
        });

        let verantwJWT = await user.create({
            voornaam: "V",
            achternaam: "Cent",
            email: "vincent@email.com",
            password: "therealslimshady2",
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

        const response = await request
            .post(siteEndpoint)
            .set({
                Authorization: `Bearer ${adminJWT}`,
            })
            .send({
                naam: "Site 10",
                verantw_id: jwtUse.getUserID(verantwJWT),
            });

        expect(response.status).toBe(200);
    });
    it("POST - non admin cannot assign other users", async () => {
        // first create a user that's a verantwoordelijke
        let adminJWT = await user.create({
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
            rol: Rol.GEBRUIKER, // Gebruiker instead of admin
        });

        let verantwJWT = await user.create({
            voornaam: "V",
            achternaam: "Cent",
            email: "vincent@email.com",
            password: "therealslimshady2",
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

        const response = await request
            .post(siteEndpoint)
            .set({
                Authorization: `Bearer ${adminJWT}`,
            })
            .send({
                naam: "Site 10",
                verantw_id: jwtUse.getUserID(verantwJWT),
            });

        expect(response.status).toBe(401);
        expect(response.body.message).toBe(errorCodes.NOACCESS);
    });
    it("POST - Admin cannot assign non verantwoordelijke", async () => {
        // first create a user that's a verantwoordelijke
        let adminJWT = await user.create({
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
            rol: Rol.ADMINISTRATOR, // Correct
        });

        let verantwJWT = await user.create({
            voornaam: "V",
            achternaam: "Cent",
            email: "vincent@email.com",
            password: "therealslimshady2",
            straat: "Corpo Plaza",
            huis_nr: "505",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Night City",
            postcode: "77704",
            land: "NUSA",
            gsm_nr: "555-1701",
            actief: true,
            rol: Rol.GEBRUIKER, // Error, should be verantwoordelijke
        });

        const response = await request
            .post(siteEndpoint)
            .set({
                Authorization: `Bearer ${adminJWT}`,
            })
            .send({
                naam: "Site 10",
                verantw_id: jwtUse.getUserID(verantwJWT),
            });

        expect(response.status).toBe(400);
        expect(response.body.message).toBe(errorCodes.INVALIDDATA);
    });
});
