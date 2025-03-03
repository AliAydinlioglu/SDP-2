import argonPassword from "../core/argonPassword";
import textCodes from "../constants/textCodes";
import jwtUse from "../core/jwtUse";
import userRepository from "../repository/user";
import { PasswordlessUser, User } from "../types/types";
import { ServiceError } from "../core/errorHandler";

// USER

// Register
// only 1 user can be created at a time.
const create = async ({ voornaam, achternaam, email, password, straat, huis_nr, postcode, stad, land, gsm_nr, actief, rol }: User, expiresInSeconds?: number): Promise<string> => {
    if (password.length < 8) {
        throw new ServiceError(textCodes.SHORTPASSWORD, 400);
    }

    let result;
    try {
        result = await userRepository.createItems([
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
                rol,
            },
        ]);
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

const updateUser = async (user_id: number, { voornaam, achternaam, email, password, straat, huis_nr, postcode, stad, land, gsm_nr, actief, rol }: Partial<User>): Promise<1 | null> => {
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
            rol,
        });

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
        voornaam: result[0].voornaam,
        achternaam: result[0].achternaam,
        email: result[0].email,
        straat: result[0].straat,
        huis_nr: result[0].huis_nr,
        postcode: result[0].postcode,
        stad: result[0].stad,
        land: result[0].land,
        gsm_nr: result[0].gsm_nr ?? undefined,
        actief: result[0].actief,
        rol: result[0].rol,
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
