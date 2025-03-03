import jwtUse from "../src/core/jwtUse";
import userService from "../src/service/user";
import userRepository from "../src/repository/user";
import resetDatabase from "../src/data/resetDatabase";
import Rol from "../src/types/rol";

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
                stad: "Night City",
                postcode: "77704",
                land: "NUSA",
                gsm_nr: "555-1701",
                actief: true,
                rol: "MANAGER" as Rol,
            },
            {
                voornaam: "Rosalind",
                achternaam: "Myers",
                email: "rosalindmyers@email.com",
                password: "paperplanes",
                straat: "Westbrook Heights",
                huis_nr: "42",
                stad: "Night City",
                postcode: "77713",
                land: "NUSA",
                gsm_nr: "555-8808",
                actief: true,
                rol: "VERANTWOORDELIJKE" as Rol,
            },
            {
                voornaam: "So",
                achternaam: "Mi",
                email: "somi@email.com",
                password: "thepretender",
                straat: "Kabuki Market",
                huis_nr: "B7",
                stad: "Night City",
                postcode: "77701",
                land: "NUSA",
                gsm_nr: "555-3301",
                actief: true,
                rol: "GEBRUIKER" as Rol,
            },
        ];

        for (let user of userData) {
            let jwt = await userService.create(user);
            jwts.push(jwt);
        }
    };

    const deleteTestData = async () => {
        await resetDatabase();
        // deleting users will delete everything else due to foreign key constraints.
        for (let jwt of jwts) {
            let id = jwtUse.getUserID(jwt);
            await userRepository.deleteItems("id", id);
        }
    };

    return { createTestData, deleteTestData, getJWTs };
};

export default testData;
