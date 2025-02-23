// src/data/seed.ts
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
    await prisma.user.createMany({
        data: [
            {
                id: 1,
                email: "geralt@example.com",
                name: "Geralt Van Rivia",
                hashed_password: "1234",
            },
            {
                id: 2,
                email: "yennefer@example.com",
                name: "Yennefer of Vengerberg",
                hashed_password: "1234567",
            },
            {
                id: 3,
                email: "triss@example.com",
                name: "Triss Merigold",
                hashed_password: "123456789",
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
