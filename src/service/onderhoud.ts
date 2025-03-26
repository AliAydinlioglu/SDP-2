import textCodes from "../constants/textCodes";
import { ServiceError } from "../core/errorHandler";
import data from "../data/index";
import logging from "../core/logging";
import { Onderhoud, Prisma } from "@prisma/client";

// Create a new onderhoud record
const create = async ({ datum, startTijd, eindTijd, technieker_id, reden, rapport, opmerkingen, status, machine_id }: Omit<Onderhoud, "id">): Promise<Onderhoud> => {
    try {
        const onderhoud = await data.prisma.onderhoud.create({
            data: {
                datum,
                startTijd,
                eindTijd,
                technieker_id,
                reden,
                rapport: rapport === null ? Prisma.JsonNull : rapport,
                opmerkingen,
                status,
                machine_id,
            },
            include: {
                technieker: true,
                Machine: true,
            },
        });
        return onderhoud;
    } catch (e) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }
};

// Find onderhoud by ID
const find = async (onderhoud_id: number): Promise<Onderhoud> => {
    const onderhoud = await data.prisma.onderhoud.findUnique({
        where: { id: onderhoud_id },
        include: {
            technieker: true,
            Machine: true,
        },
    });
    if (!onderhoud) {
        throw new ServiceError(textCodes.NOTFOUND, 404);
    }
    return onderhoud;
};

// Find all onderhoud records
const findAll = async (): Promise<Onderhoud[]> => {
    return await data.prisma.onderhoud.findMany({
        include: {
            technieker: true,
            Machine: true,
        },
    });
};

// Update onderhoud record
const updateOnderhoud = async (
    onderhoud_id: number,
    { datum, startTijd, eindTijd, technieker_id, reden, rapport, opmerkingen, status, machine_id }: Partial<Omit<Onderhoud, "id">>
): Promise<Onderhoud> => {
    try {
        const onderhoud = await data.prisma.onderhoud.update({
            where: { id: onderhoud_id },
            data: {
                datum,
                startTijd,
                eindTijd,
                technieker_id,
                reden,
                rapport: rapport === null ? Prisma.JsonNull : rapport,
                opmerkingen,
                status,
                machine_id,
            },
            include: {
                technieker: true,
                Machine: true,
            },
        });
        return onderhoud;
    } catch (e) {
        throw new ServiceError(textCodes.NOTFOUND, 404);
    }
};

// Delete onderhoud record
const deleteOnderhoud = async (onderhoud_id: number): Promise<number> => {
    try {
        await data.prisma.onderhoud.delete({
            where: { id: onderhoud_id },
        });
        return 1;
    } catch (e) {
        throw new ServiceError(textCodes.NOTFOUND, 404);
    }
};

export default {
    create,
    find,
    findAll,
    updateOnderhoud,
    deleteOnderhoud,
};
