// src/data/seed.ts
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
    await prisma.user.createMany({
        data: [
            {
                id: 1,
                email: "geralt@gmail.com",
                voornaam: "Geralt",       
                achternaam: "van Rivia",
                hashed_password: "1234",
                straat: "Kaer Morhen Path",
                huis_nr: "1",
                stad: "Kaer Morhen",
                postcode: "1000",
                land: "Kaedwen",
                gsm_nr: "0123456787",
                actief: true,
                rol: { type: "Witcher" } as any,
            },
            {
                id: 2,
                email: "yennefer@gmail.com",
                voornaam: "Yennefer",
                achternaam: "van Vengerberg",
                hashed_password: "1234567",
                straat: "Sorceress Tower",
                huis_nr: "7",
                stad: "Vengerberg",
                postcode: "2000",
                land: "Aedirn",
                gsm_nr: "0123456789",
                actief: true,
                rol: { type: "Sorceress" } as any,
            },
            {
                id: 3,
                email: "triss@gmail.com",
                voornaam: "Triss",
                achternaam: "Merigold",
                hashed_password: "123456789",
                straat: "Mage’s Quarter",
                huis_nr: "12",
                stad: "Maribor",
                postcode: "3000",
                land: "Redania",
                gsm_nr: "0123456722",
                actief: true,
                rol: { type: "Mage" } as any, 
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
