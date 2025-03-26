// src/data/seed.ts
import { PrismaClient } from "@prisma/client";
import { Rol } from "@prisma/client";
import argonPassword from "../../core/argonPassword";

const prisma = new PrismaClient();

async function main() {

    const password = await argonPassword.hashPassword('12345678');

    await prisma.user.createMany({
        data: [
            {
                id: 1,
                email: "geralt@gmail.com",
                voornaam: "Geralt",
                achternaam: "van Rivia",
                hashed_password: password,
                geboorteDatum: new Date("1990-01-01"),
                straat: "Kaer Morhen Path",
                huis_nr: "1",
                stad: "Kaer Morhen",
                postcode: "1000",
                land: "Kaedwen",
                gsm_nr: "0123456787",
                actief: true,
                rol: Rol.ADMINISTRATOR,
            },
            {
                id: 2,
                email: "yennefer@gmail.com",
                voornaam: "Yennefer",
                achternaam: "van Vengerberg",
                hashed_password: password,
                geboorteDatum: new Date("1990-01-01"),
                straat: "Sorceress Tower",
                huis_nr: "7",
                stad: "Vengerberg",
                postcode: "2000",
                land: "Aedirn",
                gsm_nr: "0123456789",
                actief: true,
                rol: Rol.GEBRUIKER,
            },
            {
                id: 3,
                email: "triss@gmail.com",
                voornaam: "Triss",
                achternaam: "Merigold",
                hashed_password: password,
                geboorteDatum: new Date("1990-01-01"),
                straat: "Mage’s Quarter",
                huis_nr: "12",
                stad: "Maribor",
                postcode: "3000",
                land: "Redania",
                gsm_nr: "0123456722",
                actief: true,
                rol: Rol.TECHNIEKER,
            },
        ],
    });

    await prisma.site.createMany({
        data: [
            {
                id: 1,
                naam: "Kaer Morhen Workshop",
                verantw_id: 1,
            },
            {
                id: 2,
                naam: "Vengerberg Laboratory",
                verantw_id: 2,
            },
        ],
    });

    await prisma.machine.createMany({
        data: [
            {
                id: 1,
                site_id: 1,
                locatie: "Main Hall",
                info: "Forge Machine",
                status: "active",
                prod_status: "running",
                uptime: 1200,
                technieker_id: 3,
                dagenSindsOnderhoud: 30,
                volgendOnderhoud: new Date("2025-12-01"),
            },
            {
                id: 2,
                site_id: 2,
                locatie: "Alchemy Room",
                info: "Potion Mixer",
                status: "active",
                prod_status: "maintenance",
                uptime: 800,
                technieker_id: 3,
                dagenSindsOnderhoud: 15,
                volgendOnderhoud: new Date("2025-11-15"),
            },
        ],
    });
}

main()
    .then(async () => {
        await prisma.$disconnect();
    })
    .catch(async (e) => {
        console.error(e);
        await prisma.$disconnect();
        process.exit(1);
    });
