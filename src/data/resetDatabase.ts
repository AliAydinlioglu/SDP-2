import data from "./index";

async function resetDatabase(): Promise<void> {
    await data.prisma.user.deleteMany();
}

export default resetDatabase;
