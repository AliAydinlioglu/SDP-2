import kpiService from "../../src/service/kpi";
import resetDatabase from "../../src/data/resetDatabase";
import userService from "../../src/service/user";
import siteService from "../../src/service/site";
import machineService from "../../src/service/machine";
import jwtUse from "../../src/core/jwtUse";
import { Rol } from "@prisma/client";

describe("KPI Service", () => {
    let verantwToken: string;
    let verantwID: number;
    let siteId: number;

    beforeEach(async () => {
        await resetDatabase();

        // Create a technician (TECHNIEKER role)
        verantwToken = await userService.create({
            voornaam: "Tech",
            achternaam: "User",
            email: "ver@example.com",
            password: "Password123",
            straat: "Tech Street",
            huis_nr: "1",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Tech City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "0000000000",
            actief: true,
            rol: Rol.VERANTWOORDELIJKE,
        });
        verantwID = jwtUse.getUserID(verantwToken);

        // create a technician
        let technician = await userService.create({
            voornaam: "Tech",
            achternaam: "User",
            email: "tech@example.com",
            password: "Password123",
            straat: "Tech Street",
            huis_nr: "1",
            geboorteDatum: new Date("1990-01-01"),
            stad: "Tech City",
            postcode: "12345",
            land: "Testland",
            gsm_nr: "0000000000",
            actief: true,
            rol: Rol.TECHNIEKER,
        });

        // Create a site (for machine creation)
        siteId = await siteService.create({
            naam: "Test Site",
            verantw_id: verantwID, // Simple assignment for testing
        });

        // Create four machines with distinct statuses:
        // Working machine (status "Draait")
        await machineService.create({
            site_id: siteId,
            locatie: "Location 1",
            info: "Working Machine",
            status: "draait",
            prod_status: "gezond", // not used in KPI calculation
            uptime: 100,
            technieker_id: verantwID,
            dagenSindsOnderhoud: 0,
            volgendOnderhoud: new Date(Date.now() + 10 * 24 * 60 * 60 * 1000),
        });

        // Machine experiencing a problem (status "falend")
        await machineService.create({
            site_id: siteId,
            locatie: "Location 2",
            info: "Failing Machine",
            status: "gestopt",
            prod_status: "nood aan onderhoud",
            uptime: 80,
            technieker_id: verantwID,
            dagenSindsOnderhoud: 2,
            volgendOnderhoud: new Date(Date.now() + 10 * 24 * 60 * 60 * 1000),
        });

        // Machine in maintenance (status "nood aan onderhoud")
        await machineService.create({
            site_id: siteId,
            locatie: "Location 3",
            info: "Maintenance Machine",
            status: "nood aan onderhoud",
            prod_status: "falend",
            uptime: 50,
            technieker_id: verantwID,
            dagenSindsOnderhoud: 5,
            volgendOnderhoud: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000),
        });

        // Machine in good condition (status "gezond")
        await machineService.create({
            site_id: siteId,
            locatie: "Location 4",
            info: "Good Condition Machine",
            status: "draait",
            prod_status: "gezond",
            uptime: 90,
            technieker_id: verantwID,
            dagenSindsOnderhoud: 1,
            volgendOnderhoud: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000),
        });
    });

    it("should return correct KPI counts", async () => {
        const kpis = await kpiService.getKPIs();

        expect(kpis.totalMachines).toBe(4);
        expect(kpis.workingMachines).toBe(2);
        expect(kpis.machinesInGoodCondition).toBe(2);
        expect(kpis.machinesFailing).toBe(1);
        expect(kpis.machinesInMaintenance).toBe(1);
        expect(kpis.totalTechnicians).toBe(1);
        expect(kpis.assignedTechnicians).toBe(1);
    });
});
