import onderhoudService from "../../src/service/Onderhoud";
import machineService from "../../src/service/machine";
import userService from "../../src/service/user";
import siteService from "../../src/service/site";
import resetDatabase from "../../src/data/resetDatabase";
import { ServiceError } from "../../src/core/errorHandler";
import textCodes from "../../src/constants/textCodes";
import jwtUse from "../../src/core/jwtUse";
import { Rol } from "@prisma/client";

describe("Onderhoud Service", () => {
    let techniekerId: number;
    let machineId: number;
    let siteId: number;

    beforeEach(async () => {
        await resetDatabase();

        // Create a technician
        const technicianData = {
            voornaam: "Tech",
            achternaam: "Nieker",
            email: "technieker@example.com",
            password: "securePassword",
            straat: "Teststraat",
            huis_nr: "10",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Teststad",
            postcode: "1234AB",
            land: "Testland",
            gsm_nr: "0612345678",
            actief: true,
            rol: Rol.TECHNIEKER,
        };
        const technicianToken = await userService.create(technicianData);
        techniekerId = jwtUse.getUserID(technicianToken);

        // Create a site manager
        const managerData = {
            voornaam: "Mana",
            achternaam: "Ger",
            email: "manager@example.com",
            password: "securePassword",
            straat: "Managerstraat",
            huis_nr: "20",
            geboorteDatum: new Date("1985-01-01"),
            stad: "Managerstad",
            postcode: "2345BC",
            land: "Managerland",
            gsm_nr: "0698765432",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        };
        const managerToken = await userService.create(managerData);
        const verantw_id = jwtUse.getUserID(managerToken);

        // Create a site that the machine belongs to
        siteId = await siteService.create({
            naam: "Test Site",
            verantw_id: verantw_id,
        });

        // Create a machine which is needed for maintenance
        const machineData = {
            site_id: siteId,
            locatie: "Building A",
            info: "Test Machine",
            status: "Operational",
            prod_status: "Active",
            uptime: 100,
            technieker_id: techniekerId,
            dagenSindsOnderhoud: 0,
            volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
        };
        const machine = await machineService.create(machineData);
        machineId = machine.id;
    });

    describe("create", () => {
        it("should create an onderhoud record successfully", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const onderhoud = await onderhoudService.create(onderhoudData);
            expect(onderhoud).toBeDefined();
            expect(onderhoud.id).toBeDefined();
            expect(onderhoud.reden).toBe(onderhoudData.reden);
            expect(onderhoud.technieker_id).toBe(onderhoudData.technieker_id);
            expect(onderhoud.machine_id).toBe(onderhoudData.machine_id);
        });

        it("should throw error when given invalid data", async () => {
            const invalidData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: 999999, // non-existent technician
                reden: "Routine Check",
                rapport: { findings: "Data invalid" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: 999999, // non-existent machine
            };

            await expect(onderhoudService.create(invalidData)).rejects.toThrow(ServiceError);
            await expect(onderhoudService.create(invalidData)).rejects.toThrow(textCodes.INVALIDDATA);
        });
    });

    describe("find", () => {
        it("should find an onderhoud record by ID", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createdOnderhoud = await onderhoudService.create(onderhoudData);
            const foundOnderhoud = await onderhoudService.find(createdOnderhoud.id);

            expect(foundOnderhoud).toBeDefined();
            expect(foundOnderhoud.id).toBe(createdOnderhoud.id);
        });

        it("should throw error for non-existent onderhoud record", async () => {
            await expect(onderhoudService.find(999999)).rejects.toThrow(ServiceError);
            await expect(onderhoudService.find(999999)).rejects.toThrow(textCodes.NOTFOUND);
        });
    });

    describe("findAll", () => {
        it("should find all onderhoud records", async () => {
            const onderhoudData1 = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const onderhoudData2 = {
                datum: new Date("2025-03-26"),
                startTijd: new Date("2025-03-26T09:00:00.000Z"),
                eindTijd: new Date("2025-03-26T13:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Repair",
                rapport: { findings: "Minor issues found" },
                opmerkingen: "Requires follow-up",
                status: "InProgress",
                machine_id: machineId,
            };

            await onderhoudService.create(onderhoudData1);
            await onderhoudService.create(onderhoudData2);

            const onderhouds = await onderhoudService.findAll();
            expect(onderhouds).toBeDefined();
            expect(Array.isArray(onderhouds)).toBe(true);
            expect(onderhouds.length).toBe(2);
        });
    });

    describe("updateOnderhoud", () => {
        it("should update an onderhoud record successfully", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createdOnderhoud = await onderhoudService.create(onderhoudData);
            const updateData = {
                reden: "Emergency Fix",
                status: "Completed",
                rapport: { findings: "Fixed issues" },
            };

            const updatedOnderhoud = await onderhoudService.updateOnderhoud(createdOnderhoud.id, updateData);
            expect(updatedOnderhoud).toBeDefined();
            expect(updatedOnderhoud.id).toBe(createdOnderhoud.id);
            expect(updatedOnderhoud.reden).toBe(updateData.reden);
            expect(updatedOnderhoud.status).toBe(updateData.status);
            expect(updatedOnderhoud.rapport).toEqual(updateData.rapport);
        });

        it("should throw error when updating non-existent onderhoud", async () => {
            const updateData = { status: "Broken" };
            await expect(onderhoudService.updateOnderhoud(999999, updateData)).rejects.toThrow(ServiceError);
            await expect(onderhoudService.updateOnderhoud(999999, updateData)).rejects.toThrow(textCodes.NOTFOUND);
        });
    });

    describe("deleteOnderhoud", () => {
        it("should delete an onderhoud record successfully", async () => {
            const onderhoudData = {
                datum: new Date("2025-03-25"),
                startTijd: new Date("2025-03-25T08:00:00.000Z"),
                eindTijd: new Date("2025-03-25T12:00:00.000Z"),
                technieker_id: techniekerId,
                reden: "Routine Check",
                rapport: { findings: "All systems operational" },
                opmerkingen: "No issues detected",
                status: "Scheduled",
                machine_id: machineId,
            };

            const createdOnderhoud = await onderhoudService.create(onderhoudData);
            const deleteResult = await onderhoudService.deleteOnderhoud(createdOnderhoud.id);
            expect(deleteResult).toBe(1);
            await expect(onderhoudService.find(createdOnderhoud.id)).rejects.toThrow(ServiceError);
            await expect(onderhoudService.find(createdOnderhoud.id)).rejects.toThrow(textCodes.NOTFOUND);
        });

        it("should throw error when deleting a non-existent onderhoud record", async () => {
            await expect(onderhoudService.deleteOnderhoud(999999)).rejects.toThrow(ServiceError);
            await expect(onderhoudService.deleteOnderhoud(999999)).rejects.toThrow(textCodes.NOTFOUND);
        });
    });
});
