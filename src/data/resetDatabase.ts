import data from "./index";

async function resetDatabase(): Promise<void> {
    await data.prisma.site.deleteMany();
    await data.prisma.machine.deleteMany();
    await data.prisma.user.deleteMany();
    await data.prisma.onderhoud.deleteMany();
    await data.prisma.melding.deleteMany();
}

export default resetDatabase;
