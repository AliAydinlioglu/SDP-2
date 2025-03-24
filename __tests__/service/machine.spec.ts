import machineService from "../../src/service/machine";
import userService from "../../src/service/user";
import siteService from "../../src/service/site";
import { ServiceError } from "../../src/core/errorHandler";
import textCodes from "../../src/constants/textCodes";
import { Rol } from "@prisma/client";
import resetDatabase from "../../src/data/resetDatabase";
import jwtUse from "../../src/core/jwtUse";

describe("Machine Service", () => {
    let siteId: number;
    let techniekerId: number;
    let machineId: number;

    beforeEach(async () => {
        await resetDatabase();
    });

    beforeEach(async () => {
        // Create a technician
        const technicianData = {
            voornaam: "tech",
            achternaam: "Nieker",
            email: "technieker@email.com",
            password: "therealslimshady",
            straat: "Corpo Plaza",
            huis_nr: "505",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Night City",
            postcode: "77704",
            land: "NUSA",
            gsm_nr: "555-1701",
            actief: true,
            rol: Rol.TECHNIEKER,
        };
        const technicianToken = await userService.create(technicianData);
        techniekerId = jwtUse.getUserID(technicianToken);

        // Create a site manager
        const managerData = {
            voornaam: "Mana",
            achternaam: "Ger",
            email: "manager@email.com",
            password: "therealslimshady",
            straat: "Corpo Plaza",
            huis_nr: "505",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Night City",
            postcode: "77704",
            land: "NUSA",
            gsm_nr: "555-1701",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        };
        const managerToken = await userService.create(managerData);
        let verantw_id = jwtUse.getUserID(managerToken);

        // Create a site
        siteId = await siteService.create({
            naam: `Test Site`,
            verantw_id: verantw_id,
        });
    });

    describe("create", () => {
        it("should create a machine successfully", async () => {
            const machineData = {
                site_id: siteId,
                locatie: "Building A",
                info: "Test Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 days from now
            };

            const machine = await machineService.create(machineData);

            expect(machine).toBeDefined();
            expect(machine.id).toBeDefined();
            expect(machine.locatie).toBe(machineData.locatie);
            expect(machine.info).toBe(machineData.info);
            expect(machine.status).toBe(machineData.status);
            expect(machine.site_id).toBe(siteId);
            expect(machine.technieker_id).toBe(techniekerId);

            // Save for later tests
            machineId = machine.id;
        });

        it("should throw error with invalid data", async () => {
            const invalidData = {
                // Missing required fields
                site_id: 999999, // Non-existent site ID
                locatie: "Invalid Location",
                info: "Test Invalid Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: 999999, // Non-existent technician ID
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
            };

            await expect(machineService.create(invalidData)).rejects.toThrow(ServiceError);
            await expect(machineService.create(invalidData)).rejects.toThrow(textCodes.INVALIDDATA);
        });
    });

    describe("find", () => {
        it("should find a machine by ID", async () => {
            let machineObj = {
                site_id: siteId,
                locatie: "Building B",
                info: "Test Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
            };

            const machine = await machineService.create(machineObj);
            machineId = machine.id;

            expect(machine).toBeDefined();
            expect(machine.id).toBe(machineId);
            expect(machine.site_id).toBeDefined();
            expect(machine.technieker_id).toBeDefined();
            expect(machine.volgendOnderhoud).toBeDefined();
        });

        it("should throw error for non-existent machine", async () => {
            await expect(machineService.find(999999)).rejects.toThrow(ServiceError);
            await expect(machineService.find(999999)).rejects.toThrow(textCodes.MACHINENOTFOUND);
        });
    });

    describe("findAll", () => {
        it("should find all machines", async () => {
            let machineObj = {
                site_id: siteId,
                locatie: "Building B",
                info: "Test Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
            };

            let machineObj2 = {
                site_id: siteId,
                locatie: "Building B",
                info: "Test Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
            };

            await machineService.create(machineObj);
            await machineService.create(machineObj2);

            const machines = await machineService.findAll();

            expect(machines).toBeDefined();
            expect(Array.isArray(machines)).toBe(true);
            expect(machines.length).toBe(2);
        });
    });

    describe("updateMachine", () => {
        it("should update a machine successfully", async () => {
            let machineObj = {
                site_id: siteId,
                locatie: "Building B",
                info: "Test Machine",
                status: "Operational",
                prod_status: "Active",
                uptime: 100,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 0,
                volgendOnderhoud: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
            };

            const machine = await machineService.create(machineObj);
            machineId = machine.id;

            const updateData = {
                locatie: "Updated Location",
                status: "Maintenance",
                prod_status: "Inactive",
                uptime: 95,
            };

            const updatedMachine = await machineService.updateMachine(machineId, updateData);

            expect(updatedMachine).toBeDefined();
            expect(updatedMachine.id).toBe(machineId);
            expect(updatedMachine.locatie).toBe(updateData.locatie);
            expect(updatedMachine.status).toBe(updateData.status);
            expect(updatedMachine.prod_status).toBe(updateData.prod_status);
            expect(updatedMachine.uptime).toBe(updateData.uptime);
        });

        it("should throw error for non-existent machine", async () => {
            const updateData = {
                status: "Broken",
            };

            await expect(machineService.updateMachine(999999, updateData)).rejects.toThrow(ServiceError);
            await expect(machineService.updateMachine(999999, updateData)).rejects.toThrow(textCodes.MACHINENOTFOUND);
        });
    });

    describe("deleteMachine", () => {
        it("should delete a machine successfully", async () => {
            // First create a new machine to delete
            const machineData = {
                site_id: siteId,
                locatie: "Building To Delete",
                info: "Machine to delete",
                status: "Operational",
                prod_status: "Active",
                uptime: 80,
                technieker_id: techniekerId,
                dagenSindsOnderhoud: 5,
                volgendOnderhoud: new Date(Date.now() + 25 * 24 * 60 * 60 * 1000),
            };

            const machine = await machineService.create(machineData);
            const deleteResult = await machineService.deleteMachine(machine.id);

            expect(deleteResult).toBe(1);

            // Verify it's deleted by trying to find it
            await expect(machineService.find(machine.id)).rejects.toThrow(ServiceError);
            await expect(machineService.find(machine.id)).rejects.toThrow(textCodes.MACHINENOTFOUND);
        });

        it("should throw error for non-existent machine", async () => {
            await expect(machineService.deleteMachine(999999)).rejects.toThrow(ServiceError);
            await expect(machineService.deleteMachine(999999)).rejects.toThrow(textCodes.MACHINENOTFOUND);
        });
    });
});
