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
                achternaam: "Van Rivia",
                hashed_password: "1234",
                adres: "Kouterstraat 24, Zele",        
                actief: true,
            },
            {
                id: 2,
                email: "yennefer@gmail.com",
                voornaam: "Yennefer",
                achternaam: "of Vengerberg",
                hashed_password: "1234567",
                adres: "Kouterstraat 25, Zele",        
                gsm: "0123456789",          
                actief: true,
            },
            {
                id: 3,
                email: "triss@gmail.com",
                voornaam: "Triss",
                achternaam: "Merigold",
                hashed_password: "123456789",
                adres: "Kouterstraat 26, Zele",        
                gsm: "0123456722",          
                actief: true,

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
