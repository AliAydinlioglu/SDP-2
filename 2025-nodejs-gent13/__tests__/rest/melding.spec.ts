// filepath: test/rest/melding.test.ts
import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import errorCodes from "../../src/constants/textCodes";
import endpoints from "../../src/constants/endpoints";
import melding from "../../src/service/melding";
import user from "../../src/service/user";
import { Rol } from "@prisma/client";
import jwtUse from "../../src/core/jwtUse";
import resetDatabase from "../../src/data/resetDatabase";

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

describe("/meldingen", () => {
    it("Should retrieve a melding by ID", async () => {
        // Create a user
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
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);

        let melding_id = await melding.create({ 
            beschrijving: "Test Melding", 
            user_id: id,
            status: "Nieuw",
            type: "Storing",
            datum: new Date()
        });

        let meldingObject = await melding.find(melding_id);

        expect(meldingObject!.beschrijving).toBe("Test Melding");
        expect(meldingObject!.user_id).toBe(id);
        expect(meldingObject!.status).toBe("Nieuw");
        expect(meldingObject!.type).toBe("Storing");
    });

    it("Should create a new melding", async () => {
        // Create a user
        let jwt = await user.create({
            voornaam: "Jane",
            achternaam: "Smith",
            email: "jane.smith@email.com",
            password: "password123",
            straat: "Oak Avenue",
            huis_nr: "15",
            geboorteDatum: new Date("1990-03-20"),
            stad: "Antwerp",
            postcode: "2000",
            land: "Belgium",
            gsm_nr: "555-5678",
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);
        
        const now = new Date();
        
        let melding_id = await melding.create({
            beschrijving: "New Melding",
            user_id: id,
            status: "In Behandeling",
            type: "Onderhoud",
            datum: now
        });

        let meldingObject = await melding.find(melding_id);
        const diff = Math.abs(new Date(meldingObject!.datum).getTime() - now.getTime());
        
        expect(meldingObject!.beschrijving).toBe("New Melding");
        expect(meldingObject!.user_id).toBe(id);
        expect(meldingObject!.status).toBe("In Behandeling");
        expect(meldingObject!.type).toBe("Onderhoud");
        expect(diff).toBeLessThan(1500); // past de tolerantie aan als dat nodig is
      
    });

    it("Should update a melding", async () => {
        // Create a user
        let jwt = await user.create({
            voornaam: "Alice",
            achternaam: "Johnson",
            email: "alice.johnson@email.com",
            password: "securepwd",
            straat: "Maple Street",
            huis_nr: "25",
            geboorteDatum: new Date("1988-11-30"),
            stad: "Ghent",
            postcode: "9000",
            land: "Belgium",
            gsm_nr: "555-9876",
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);

        let melding_id = await melding.create({ 
            beschrijving: "Old Description", 
            user_id: id,
            status: "Nieuw",
            type: "Storing",
            datum: new Date()
        });

        await melding.update(melding_id, { 
            beschrijving: "Updated Description",
            status: "Opgelost" 
        });

        let meldingObject = await melding.find(melding_id);
        expect(meldingObject!.beschrijving).toBe("Updated Description");
        expect(meldingObject!.status).toBe("Opgelost");
    });

    it("Should not update a melding with invalid data", async () => {
        // Create a user
        let jwt = await user.create({
            voornaam: "Bob",
            achternaam: "Williams",
            email: "bob.williams@email.com",
            password: "ez12345678",
            straat: "Pine Road",
            huis_nr: "40",
            geboorteDatum: new Date("1975-05-10"),
            stad: "Bruges",
            postcode: "8000",
            land: "Belgium",
            gsm_nr: "555-4321",
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);

        let melding_id = await melding.create({ 
            beschrijving: "Original Description", 
            user_id: id,
            status: "Nieuw",
            type: "Storing",
            datum: new Date()
        });

        try {
            await melding.update(melding_id, { beschrijving: "" }); // Invalid description
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.INVALIDDATA);
        }
    });

    it("Should delete a melding", async () => {
        // Create a user
        let jwt = await user.create({
            voornaam: "Charlie",
            achternaam: "Brown",
            email: "charlie.brown@email.com",
            password: "snoopypass",
            straat: "Cartoon Street",
            huis_nr: "50",
            geboorteDatum: new Date("1980-12-20"),
            stad: "Leuven",
            postcode: "3000",
            land: "Belgium",
            gsm_nr: "555-1122",
            actief: true,
            rol: Rol.GEBRUIKER,
        });

        let id = jwtUse.getUserID(jwt);

        let melding_id = await melding.create({ 
            beschrijving: "Temporary Melding", 
            user_id: id,
            status: "Nieuw",
            type: "Vraag",
            datum: new Date()
        });

        await melding.deleteMelding(melding_id);

        try {
            await melding.find(melding_id);
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.MELDINGNOTFOUND);
        }
    });

    it("Should not delete a non-existing melding", async () => {
        try {
            await melding.deleteMelding(99999); // Non-existent melding ID
            expect(true).toBe(false);
        } catch (e: any) {
            expect(e.message).toBe(errorCodes.MELDINGNOTFOUND);
        }
    });
});