import { ServiceError } from "../core/errorHandler";
import textCodes from "../constants/textCodes";
import data from "../data/index";
import { Melding } from "@prisma/client";
import userService from "./user";

// Create a new melding
const create = async ({ 
    beschrijving, 
    user_id, 
    status, 
    type, 
    datum 
}: { 
    beschrijving: string; 
    user_id: number; 
    status: string; 
    type: string; 
    datum: Date;
}): Promise<number> => {
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
                    datum
                },
            })
        ).id;
    } catch (e) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }
    
    return id;
};

// Update an existing melding
const update = async (
    melding_id: number,
    { beschrijving, user_id, status, type, datum }: Partial<{ 
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
            datum
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
    
    return result;
};

// Get all meldingen
const findAll = async (): Promise<Melding[]> => {
    return await data.prisma.melding.findMany();
};

// Delete a melding
const deleteMelding = async (melding_id: number): Promise<number> => {
    try {
        await data.prisma.melding.delete({ where: { id: melding_id } });
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