import textCodes from "../constants/textCodes";
import { ServiceError } from "../core/errorHandler";
import data from "../data/index";
import logging from "../core/logging";
import { Machine } from "@prisma/client";

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

    return machine;
};

// Find all machines
const findAll = async (): Promise<Machine[]> => {
    return await data.prisma.machine.findMany({
        include: {
            site: true,
            technieker: true,
        },
    });
};

// Update machine
const updateMachine = async (
    machine_id: number,
    { site_id, locatie, info, status, prod_status, uptime, technieker_id, dagenSindsOnderhoud, volgendOnderhoud }: Partial<Omit<Machine, "id" | "site" | "technieker" | "onderhouden">>
): Promise<Machine> => {
    try {
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
        return machine;
    } catch (e) {
        throw new ServiceError(textCodes.MACHINENOTFOUND, 404);
    }
};

// Delete machine
const deleteMachine = async (machine_id: number): Promise<number> => {
    try {
        await data.prisma.machine.delete({
            where: { id: machine_id },
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
