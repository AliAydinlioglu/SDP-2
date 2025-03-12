// filepath: /home/oguz/repos/NOTES/TI2425/SDP2/2025-nodejs-gent13/src/service/site.ts
import { ServiceError } from "../core/errorHandler";
import textCodes from "../constants/textCodes";
import data from "../data/index";
import { Rol, Site } from "@prisma/client";
import userService from "./user";

// Create a new site
const create = async ({ naam, verantw_id }: { naam: string; verantw_id: number }): Promise<number> => {
    let user = await userService.find(verantw_id);

    if (user.rol !== Rol.VERANTWOORDELIJKE) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    let id;
    try {
        id = (
            await data.prisma.site.create({
                data: {
                    naam,
                    verantw_id: verantw_id,
                },
            })
        ).id;
    } catch (e) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    return id;
};

// Update an existing site
const update = async (
    site_id: number,
    { naam, verantw_id }: Partial<{ naam: string; verantw_id: number }>
): Promise<1 | null> => {
    // Check if the site exists
    const existingSite = await data.prisma.site.findUnique({
        where: { id: site_id },
    });
    if (!existingSite) {
        throw new ServiceError(textCodes.SITENOTFOUND, 404);
    }

    // Validate naam (should not be empty)
    if (naam !== undefined && naam.trim() === "") {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    // Validate verantw_id
    if (verantw_id) {
        let user = await userService.find(verantw_id);
        if (!user || user.rol !== Rol.VERANTWOORDELIJKE) {
            throw new ServiceError(textCodes.INVALIDDATA, 400);
        }
    }

    // Update site
    await data.prisma.site.update({
        where: { id: site_id },
        data: {
            naam,
            verantw_id,
        },
    });

    return 1;
};


// Find a site by ID
const find = async (site_id: number): Promise<Site | null> => {
    const result = await data.prisma.site.findUnique({ where: { id: site_id } });

    if (!result) {
        throw new ServiceError(textCodes.SITENOTFOUND, 404);
    }

    return result;
};

// Delete a site
const deleteSite = async (site_id: number): Promise<number> => {
    try {
        await data.prisma.site.delete({ where: { id: site_id } });
    } catch (e) {
        throw new ServiceError(textCodes.SITENOTFOUND, 404);
    }

    return 1;
};

export default {
    create,
    update,
    find,
    deleteSite,
};
