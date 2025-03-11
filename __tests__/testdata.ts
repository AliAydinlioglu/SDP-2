import jwtUse from "../src/core/jwtUse";
import userService from "../src/service/user";
import resetDatabase from "../src/data/resetDatabase";
import { Rol } from "@prisma/client";
import data from "../src/data";

// TEST DATA

let jwts: string[] = [];

const testData = async () => {
    const getJWTs = () => {
        return jwts;
    };

    const createTestData = async () => {
        jwts = [];
        let userData = [
            {
                voornaam: "Solomon",
                achternaam: "Reed",
                email: "solomonreed@email.com",
                password: "therealslimshady",
                straat: "Corpo Plaza",
                huis_nr: "505",
                geboorteDatum: new Date("1990-01-01"),
                stad: "Night City",
                postcode: "77704",
                land: "NUSA",
                gsm_nr: "555-1701",
                actief: true,
                rol: Rol.MANAGER,
            },
            {
                voornaam: "Rosalind",
                achternaam: "Myers",
                email: "rosalindmyers@email.com",
                password: "paperplanes",
                straat: "Westbrook Heights",
                huis_nr: "42",
                geboorteDatum: new Date("1990-01-01"),
                stad: "Night City",
                postcode: "77713",
                land: "NUSA",
                gsm_nr: "555-8808",
                actief: true,
                rol: Rol.VERANTWOORDELIJKE,
            },
            {
                voornaam: "So",
                achternaam: "Mi",
                email: "somi@email.com",
                password: "thepretender",
                straat: "Kabuki Market",
                huis_nr: "B7",
                geboorteDatum: new Date("1990-01-01"),
                stad: "Night City",
                postcode: "77701",
                land: "NUSA",
                gsm_nr: "555-3301",
                actief: true,
                rol: Rol.GEBRUIKER,
            },
        ];

        for (let user of userData) {
            let jwt = await userService.create(user);
            jwts.push(jwt);
        }
    };

    return { createTestData, getJWTs };
};

export default testData;
