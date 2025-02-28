import argonPassword from "../core/argonPassword";
import textCodes from "../constants/textCodes";
import jwtUse from "../core/jwtUse";
import userRepository from "../repository/user";
import { PasswordlessUser } from "../types/types";
import { ServiceError } from "../core/errorHandler";
import Rol from "../constants/rol";

// USER

// Register
const create = async ({
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
    voornaam: string;
    achternaam: string;
    email: string;
    password: string;
    straat: string;
    huis_nr: string;
    postcode: string;
    stad: string;
    land: string;
    gsm_nr?: string;
    actief?: boolean;
    rol: Rol;
}, expiresInSeconds?: number): Promise<string> => {
    if (password.length < 8) {
        throw new ServiceError(textCodes.SHORTPASSWORD, 400);
    }

    let result;
    try {
        result = await userRepository.createItems([{
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
        }]);
    } catch (e) {
        throw new ServiceError(textCodes.DUPLICATE, 400);
    }

    if (!result) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    let id = result[0];
    let token = jwtUse.generateJWT(id, expiresInSeconds);
    return token;
};

// Login
const login = async ({ email, password }: { email: string; password: string }): Promise<string> => {
    let result = await userRepository.find("email", email);

    if (!result) {
        return textCodes.NOUSERFOUND;
    } else if (await argonPassword.verifyPassword(password, result[0].hashed_password)) {
        return jwtUse.generateJWT(result[0].id);
    } else {
        return textCodes.WRONGPASSWORD;
    }
};

const updateUser = async (user_id: number, {
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
}): Promise<1 | null> => {
    try {
        let result = await userRepository.updateItem(user_id, {
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
        });

        if (!result) {
            throw new ServiceError(textCodes.INVALIDDATA, 404);
        }

        return result;
    } catch (e) {
        throw new ServiceError(textCodes.EMAILALREADYEXISTS, 405);
    }
};

const find = async (user_id: number): Promise<PasswordlessUser | null> => {
    const result = await userRepository.find("id", user_id);

    if (!result || result.length === 0) return null;

    return {
        id: result[0].id,
        voornaam: result[0].voornaam,
        achternaam: result[0].achternaam,
        email: result[0].email,
        straat: result[0].straat,
        huis_nr: result[0].huis_nr,
        postcode: result[0].postcode,
        stad: result[0].stad,
        land: result[0].land,
        gsm_nr: result[0].gsm_nr || undefined,
        actief: result[0].actief,
        rol: result[0].rol
    };
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