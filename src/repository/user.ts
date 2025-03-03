import crypto from "../core/argonPassword";
import { DBUser, User } from "../types/types";
import data from "../data/index";

// Create
// .insert() will return the newly created id (autoincrement is enabled).
const createItems = async (users: User[]): Promise<number[]> => {
    return Promise.all(
        users.map(
            async (u) =>
                (
                    await data.prisma.user.create({
                        data: {
                            voornaam: u.voornaam,
                            achternaam: u.achternaam,
                            email: u.email.toLowerCase(),
                            hashed_password: await crypto.hashPassword(u.password),
                            straat: u.straat,
                            huis_nr: u.huis_nr,
                            postcode: u.postcode,
                            stad: u.stad,
                            land: u.land,
                            gsm_nr: u.gsm_nr,
                            actief: u.actief ?? true,
                            rol: u.rol,
                        },
                    })
                ).id
        )
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
        return users.length ? (users as DBUser[]) : null;
    }
    return user ? [user as DBUser] : null;
};

// .select() will return array of objects where the objects are the rows from the database.
const FindAll = async () => {
    return data.prisma.user.findMany();
};

// Update
const updateItem = async (id: number, { voornaam, achternaam, email, password, straat, huis_nr, postcode, stad, land, gsm_nr, actief, rol }: Partial<User>): Promise<1 | null> => {
    const update: Record<string, any> = {};
    if (voornaam) update.voornaam = voornaam;
    if (achternaam) update.achternaam = achternaam;
    if (email) update.email = email.toLowerCase();
    if (password) update.hashed_password = await crypto.hashPassword(password);
    if (straat) update.straat = straat;
    if (huis_nr) update.huis_nr = huis_nr;
    if (postcode) update.postcode = postcode;
    if (stad) update.stad = stad;
    if (land) update.land = land;
    if (gsm_nr !== undefined) update.gsm_nr = gsm_nr;
    if (actief !== undefined) update.actief = actief;
    if (rol) update.rol = rol;

    try {
        await data.prisma.user.update({ where: { id }, data: update });
        return 1;
    } catch {
        return null;
    }
};

// Delete
// IT IS CALLED deleteItem(S) because it deletes a single item OR EVERYTHING!
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
