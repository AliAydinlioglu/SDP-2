import argonPassword from "../core/argonPassword";
import textCodes from "../constants/textCodes";
import jwtUse from "../core/jwtUse";
import userRepository from "../repository/user";
import { PasswordlessUser } from "../types/types";
import { ServiceError } from "../core/errorHandler";

// USER

// Register
// only 1 user can be created at a time.
const create = async ({ name, email, password }: { name: string; email: string; password: string }, expiresInSeconds?: number): Promise<string> => {
    if (password.length < 8) {
        throw new ServiceError(textCodes.SHORTPASSWORD, 400);
    }

    let result;
    try {
        result = await userRepository.createItems([{ name, email, password }]);
    } catch (e) {
        throw new ServiceError(textCodes.DUPLICATE, 400);
    }

    if (result === null || result == undefined) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    let id = result[0];
    let token = jwtUse.generateJWT(id, expiresInSeconds);
    return token;
};

// Login
const login = async ({ email, password }: { email: string; password: string }): Promise<string> => {
    let result = await userRepository.find("email", email);

    if (result === null) {
        // user not found
        return textCodes.NOUSERFOUND;
    } else if (await argonPassword.verifyPassword(password, result[0].hashed_password)) {
        return jwtUse.generateJWT(result[0].id); // user found and password is correct, return a token.
    } else {
        return textCodes.WRONGPASSWORD; // user found but password is incorrect.
    }
};

const updateUser = async (user_id: number, { name, email, password }: { name?: string; email?: string; password?: string }): Promise<1 | null> => {
    try {
        let result = await userRepository.updateItem(user_id, { name, email, password });

        if (result === null) {
            throw new ServiceError(textCodes.INVALIDDATA, 404);
        }

        return result;
    } catch (e) {
        throw new ServiceError(textCodes.EMAILALREADYEXISTS, 405);
    }
};

const find = async (user_id: number): Promise<PasswordlessUser | null> => {
    const result = await userRepository.find("id", user_id);

    if (result === null) {
        return null;
    }

    let passwordlessUser = {
        id: result[0].id,
        name: result[0].name,
        email: result[0].email,
    };

    return passwordlessUser;
};

const deleteUser = async (user_id: number): Promise<number> => {
    let amountOfUsers = await userRepository.deleteItems("id", user_id);

    if (amountOfUsers === 0) {
        throw new ServiceError(textCodes.USERMISSING, 404);
    }

    return amountOfUsers;
};
export default {
    create,
    login,
    updateUser,
    find,
    deleteUser,
};
