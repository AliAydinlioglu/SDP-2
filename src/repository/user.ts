import crypto from "../core/argonPassword";
import data from "../data/index";
import { DBUser, User } from "../types/types";

// Create
// .insert() will return the newly created id (autoincrement is enabled).
const createItems = async (users: User[]): Promise<number[] | null> => {
    const ids = []; // MySql will return only the last id, so we need to store all the ids and return them.

    for (const user of users) {
        const hashedUser = {
            name: user.name,
            email: user.email.toLowerCase(),
            hashed_password: await crypto.hashPassword(user.password),
        };

        let [id] = await data.getKnex()(data.tables.user).insert(hashedUser);

        ids.push(id);
    }
    return ids;
};

// Read
const find = async (attributeName: string, attributeValue: string | number): Promise<null | DBUser[]> => {
    const result = await data.getKnex()(data.tables.user).select().where(attributeName, attributeValue);

    if (result.length === 0) {
        return null;
    } else {
        return result;
    }
};

// .select() will return array of objects where the objects are the rows from the database.
const devFindAll = async () => {
    return await data.getKnex()(data.tables.user).select();
};

// Update
const updateItem = async (id: number, { name, email, password }: { name?: string; email?: string; password?: string }): Promise<null | 1> => {
    const updatedUser: { name?: string; email?: string; hashed_password?: string } = {};

    if (name) {
        updatedUser.name = name;
    }
    if (email) {
        updatedUser.email = email.toLowerCase();
    }
    if (password) {
        updatedUser.hashed_password = await crypto.hashPassword(password);
    }

    return await data.getKnex()(data.tables.user).where("id", id).update(updatedUser);
};

// Delete
// IT IS CALLED deleteItem(S) because it deletes a single item OR EVERYTHING!
const deleteItems = async (attributeName?: string, attributeValue?: number | string): Promise<number> => {
    if (attributeName && attributeValue) {
        return await data.getKnex()(data.tables.user).where(attributeName, attributeValue).delete(); // will delete by matching the attribute name and value.
    } else {
        return await data.getKnex()(data.tables.user).delete(); // delete all
    }
};

export default { createItems, devFindAll, find, updateItem, deleteItems };
