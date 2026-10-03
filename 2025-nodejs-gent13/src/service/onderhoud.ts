import textCodes from "../constants/textCodes";
import { ServiceError } from "../core/errorHandler";
import data from "../data/index";
import { Onderhoud, Prisma, Action } from "@prisma/client";
import { logCreate } from "../core/auditLog";

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

        // Log the create action
        logCreate({
            userId: technieker_id,
            machineId: machine_id,
            details: { event: "Maintenance created", onderhoudId: onderhoud.id, status },
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

    // Log the read action
    logCreate({
        userId: onderhoud.technieker_id,
        machineId: onderhoud.machine_id,
        details: { event: "Maintenance read", onderhoudId: onderhoud_id },
    });

    return onderhoud;
};

// Find all onderhoud records
const findAll = async (): Promise<Onderhoud[]> => {
    const onderhoudRecords = await data.prisma.onderhoud.findMany({
        include: {
            technieker: true,
            Machine: true,
        },
    });

    // Log the read all action (using 0 as a system user ID for global operations)
    logCreate({
        userId: 0,
        details: { event: "All maintenance records retrieved", count: onderhoudRecords.length },
    });

    return onderhoudRecords;
};

// Update onderhoud record
const updateOnderhoud = async (
    onderhoud_id: number,
    { datum, startTijd, eindTijd, technieker_id, reden, rapport, opmerkingen, status, machine_id }: Partial<Omit<Onderhoud, "id">>
): Promise<Onderhoud> => {
    try {
        // Get the current record first to have needed IDs for logging
        const existingRecord = await data.prisma.onderhoud.findUnique({
            where: { id: onderhoud_id },
        });

        if (!existingRecord) {
            throw new ServiceError(textCodes.NOTFOUND, 404);
        }

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

        // Log the update action
        logCreate({
            userId: technieker_id || existingRecord.technieker_id,
            machineId: machine_id || existingRecord.machine_id,
            details: {
                event: "Maintenance updated",
                onderhoudId: onderhoud_id,
                updatedFields: {
                    status,
                    datum: datum ? datum.toISOString() : undefined,
                    technieker_id,
                    machine_id,
                },
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
        // Get the record first to have needed IDs for logging
        const recordToDelete = await data.prisma.onderhoud.findUnique({
            where: { id: onderhoud_id },
        });

        if (!recordToDelete) {
            throw new ServiceError(textCodes.NOTFOUND, 404);
        }

        await data.prisma.onderhoud.delete({
            where: { id: onderhoud_id },
        });

        // Log the delete action
        logCreate({
            userId: recordToDelete.technieker_id,
            machineId: recordToDelete.machine_id,
            details: { event: "Maintenance deleted", onderhoudId: onderhoud_id },
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
