import crypto from "../core/argonPassword";
import { DBUser, User } from "../types/types";
import data from "../data/index";
import Rol from "../constants/rol";

// Create
const createItems = async (users: User[]): Promise<number[]> => {
    return Promise.all(
        users.map(async (u) => {
            const createdUser = await data.prisma.user.create({
                data: {
                    voornaam: u.voornaam,
                    achternaam: u.achternaam,
                    email: u.email.toLowerCase(),
                    hashed_password: await crypto.hashPassword(u.password),
                    straat: u.straat,
                    huis_nr: u.huis_nr,
                    stad: u.stad,
                    postcode: u.postcode,
                    land: u.land,
                    gsm_nr: u.gsm_nr,
                    actief: u.actief ?? true,
                    rol: u.rol,
                },
            });
            return createdUser.id;
        })
    );
};

// Read
const find = async (attribute: string, value: string | number): Promise<DBUser[] | null> => {
    let user;

    if (attribute === "id") {
        value = Number(value);
        user = await data.prisma.user.findUnique({ where: { id: value as number } });
    } else if (attribute === "email") {
        user = await data.prisma.user.findUnique({ where: { email: (value as string).toLowerCase() } });
    } else {
        const users = await data.prisma.user.findMany({ where: { [attribute]: value } });
        return users.length ? users.map(mapToDBUser) : null;
    }

    return user ? [mapToDBUser(user)] : null;
};

const mapToDBUser = (user: any): DBUser => ({
    ...user,
    rol: user.rol as Rol, 
    adres: `${user.straat} ${user.huis_nr}, ${user.postcode} ${user.stad}, ${user.land}`,
});

// Find All
const FindAll = async () => {
    return data.prisma.user.findMany();
};

// Update
const updateItem = async (
    id: number,
    {
        voornaam,
        achternaam,
        email,
        password,
        straat,
        huis_nr,
        postcode,
        stad,
        land,
        gsm_nr,
        actief,
        rol
    }: {
        voornaam?: string;
        achternaam?: string;
        email?: string;
        password?: string;
        straat?: string;
        huis_nr?: string;
        postcode?: string;
        stad?: string;
        land?: string;
        gsm_nr?: string;
        actief?: boolean;
        rol?: Rol;
    }
): Promise<1 | null> => {
    const update: Record<string, any> = {};

    if (voornaam) update.voornaam = voornaam;
    if (achternaam) update.achternaam = achternaam;
    if (email) update.email = email.toLowerCase();
    if (password) update.hashed_password = await crypto.hashPassword(password);
    if (gsm_nr) update.gsm_nr = gsm_nr;
    if (actief !== undefined) update.actief = actief;
    if (straat) update.straat = straat;
    if (huis_nr) update.huis_nr = huis_nr;
    if (postcode) update.postcode = postcode;
    if (stad) update.stad = stad;
    if (land) update.land = land;
    if (rol) update.rol = rol;

    try {
        await data.prisma.user.update({ where: { id }, data: update });
        return 1;
    } catch (error) {
        console.error("Fout bij updaten van gebruiker:", error);
        return null;
    }
};

// Delete
const deleteItems = async (attribute?: string, value?: number | string): Promise<number> => {
    try {
        if (attribute && value) {
            if (attribute === "id") await data.prisma.user.delete({ where: { id: value as number } });
            else if (attribute === "email") await data.prisma.user.delete({ where: { email: (value as string).toLowerCase() } });
            return 1;
        }
    } catch {
        return 0;
    }

    return (await data.prisma.user.deleteMany()).count;
};

export default { createItems, FindAll, find, updateItem, deleteItems };
