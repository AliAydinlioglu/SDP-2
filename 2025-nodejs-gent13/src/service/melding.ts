import { ServiceError } from "../core/errorHandler";
import textCodes from "../constants/textCodes";
import data from "../data/index";
import { Action, Melding } from "@prisma/client";
import userService from "./user";
import { logCreate } from "../core/auditLog";

// Create a new melding
const create = async ({ beschrijving, user_id, status, type, datum }: { beschrijving: string; user_id: number; status: string; type: string; datum: Date }): Promise<number> => {
    // Check if user exists
    await userService.find(user_id);

    let id;
    try {
        id = (
            await data.prisma.melding.create({
                data: {
                    beschrijving,
                    user_id,
                    status,
                    type,
                    datum,
                },
            })
        ).id;

        // Log the creation action
        logCreate({
            userId: user_id,
            details: { event: "Melding created", meldingId: id, type, status },
        });
    } catch (e) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    return id;
};

// Update an existing melding
const update = async (
    melding_id: number,
    {
        beschrijving,
        user_id,
        status,
        type,
        datum,
    }: Partial<{
        beschrijving: string;
        user_id: number;
        status: string;
        type: string;
        datum: Date;
    }>
): Promise<1 | null> => {
    // Check if the melding exists
    const existingMelding = await data.prisma.melding.findUnique({
        where: { id: melding_id },
    });
    if (!existingMelding) {
        throw new ServiceError(textCodes.MELDINGNOTFOUND, 404);
    }

    // Validate beschrijving
    if (beschrijving !== undefined && beschrijving.trim() === "") {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    // Validate user_id
    if (user_id) {
        await userService.find(user_id);
    }

    // Update melding
    await data.prisma.melding.update({
        where: { id: melding_id },
        data: {
            beschrijving,
            user_id,
            status,
            type,
            datum,
        },
    });

    // Log the update action
    logCreate({
        userId: user_id || existingMelding.user_id,
        details: {
            event: "Melding updated",
            meldingId: melding_id,
            updatedFields: {
                beschrijving: beschrijving ? true : undefined,
                status,
                type,
                datum: datum ? datum.toISOString() : undefined,
            },
        },
    });

    return 1;
};

// Find a melding by ID
const find = async (melding_id: number): Promise<Melding | null> => {
    const result = await data.prisma.melding.findUnique({ where: { id: melding_id } });

    if (!result) {
        throw new ServiceError(textCodes.MELDINGNOTFOUND, 404);
    }

    // Log the read action
    logCreate({
        userId: result.user_id,
        details: { event: "Melding read", meldingId: melding_id },
    });

    return result;
};

// Get all meldingen
const findAll = async (): Promise<Melding[]> => {
    const results = await data.prisma.melding.findMany();

    // Log the read all action (using 0 as system user ID for global operations)
    logCreate({
        userId: 0,
        details: { event: "All meldingen retrieved", count: results.length },
    });

    return results;
};

// Delete a melding
const deleteMelding = async (melding_id: number): Promise<number> => {
    try {
        // Get the melding first to have user_id for logging
        const meldingToDelete = await data.prisma.melding.findUnique({
            where: { id: melding_id },
        });

        if (!meldingToDelete) {
            throw new ServiceError(textCodes.MELDINGNOTFOUND, 404);
        }

        await data.prisma.melding.delete({ where: { id: melding_id } });

        // Log the delete action
        logCreate({
            userId: meldingToDelete.user_id,
            details: { event: "Melding deleted", meldingId: melding_id },
        });
    } catch (e) {
        throw new ServiceError(textCodes.MELDINGNOTFOUND, 404);
    }

    return 1;
};

export default {
    create,
    update,
    find,
    findAll,
    deleteMelding,
};
