import textCodes from "../constants/textCodes";
import { ServiceError } from "../core/errorHandler";
import data from "../data/index";
import logging from "../core/logging";
import { Machine } from "@prisma/client";
import { logCreate } from "../core/auditLog";

// Create a new machine
const create = async ({
    site_id,
    locatie,
    info,
    status,
    prod_status,
    uptime,
    technieker_id,
    dagenSindsOnderhoud,
    volgendOnderhoud,
}: Omit<Machine, "id" | "site" | "technieker" | "onderhouden">): Promise<Machine> => {
    try {
        const machine = await data.prisma.machine.create({
            data: {
                site_id,
                locatie,
                info,
                status: status.toLocaleLowerCase(),
                prod_status: prod_status.toLocaleLowerCase(),
                uptime,
                technieker_id,
                dagenSindsOnderhoud,
                volgendOnderhoud,
            },
            include: {
                site: true,
                technieker: true,
            },
        });

        // Log the creation action
        logCreate({
            userId: technieker_id,
            machineId: machine.id,
            details: { event: "Machine created", siteId: site_id, location: locatie },
        });

        return machine;
    } catch (e) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }
};

// Find machine by ID
const find = async (machine_id: number): Promise<Machine> => {
    const machine = await data.prisma.machine.findUnique({
        where: { id: machine_id },
        include: {
            site: true,
            technieker: true,
            onderhouden: true,
        },
    });

    if (!machine) {
        throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
    }

    // Log the read action
    logCreate({
        userId: machine.technieker_id,
        machineId: machine_id,
        details: { event: "Machine read" },
    });

    return machine;
};

// Find all machines
const findAll = async (): Promise<Machine[]> => {
    const machines = await data.prisma.machine.findMany({
        include: {
            site: true,
            technieker: true,
        },
    });

    // Log the read all action (using 0 as system user ID for global operations)
    logCreate({
        userId: 0,
        details: { event: "All machines retrieved", count: machines.length },
    });

    return machines;
};

// Update machine
const updateMachine = async (
    machine_id: number,
    { site_id, locatie, info, status, prod_status, uptime, technieker_id, dagenSindsOnderhoud, volgendOnderhoud }: Partial<Omit<Machine, "id" | "site" | "technieker" | "onderhouden">>
): Promise<Machine> => {
    try {
        // Get current machine to have the technieker_id if not provided in update
        const currentMachine = await data.prisma.machine.findUnique({
            where: { id: machine_id },
        });

        if (!currentMachine) {
            throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
        }

        const machine = await data.prisma.machine.update({
            where: { id: machine_id },
            data: {
                site_id,
                locatie,
                info,
                status,
                prod_status,
                uptime,
                technieker_id,
                dagenSindsOnderhoud,
                volgendOnderhoud,
            },
            include: {
                site: true,
                technieker: true,
            },
        });

        // Log the update action
        logCreate({
            userId: technieker_id || currentMachine.technieker_id,
            machineId: machine_id,
            details: {
                event: "Machine updated",
                updatedFields: {
                    site_id,
                    locatie,
                    status,
                    prod_status,
                    technieker_id,
                },
            },
        });

        return machine;
    } catch (e) {
        throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
    }
};

// Delete machine
const deleteMachine = async (machine_id: number): Promise<number> => {
    try {
        // Get machine first to have technieker_id for logging
        const machineToDelete = await data.prisma.machine.findUnique({
            where: { id: machine_id },
        });

        if (!machineToDelete) {
            throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
        }

        await data.prisma.machine.delete({
            where: { id: machine_id },
        });

        // Log the delete action
        logCreate({
            userId: machineToDelete.technieker_id,
            machineId: machine_id,
            details: {
                event: "Machine deleted",
                siteId: machineToDelete.site_id,
                location: machineToDelete.locatie,
            },
        });

        return 1;
    } catch (e) {
        throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
    }
};

export default {
    create,
    find,
    findAll,
    updateMachine,
    deleteMachine,
};
