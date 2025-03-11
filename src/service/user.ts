import argonPassword from "../core/argonPassword";
import textCodes from "../constants/textCodes";
import jwtUse from "../core/jwtUse";
import { DBUser, PasswordlessUser, User } from "../types/types";
import { ServiceError } from "../core/errorHandler";
import data from "../data/index";

// USER

// Register
// only 1 user can be created at a time.
const create = async ({ voornaam, achternaam, email, password, geboorteDatum, straat, huis_nr, postcode, stad, land, gsm_nr, actief, rol }: User, expiresInSeconds?: number): Promise<string> => {
    if (password.length < 8) {
        throw new ServiceError(textCodes.SHORTPASSWORD, 400);
    }

    let id;
    try {
        id = (
            await data.prisma.user.create({
                data: {
                    voornaam: voornaam,
                    achternaam: achternaam,
                    email: email.toLowerCase(),
                    hashed_password: await argonPassword.hashPassword(password),
                    straat: straat,
                    huis_nr: huis_nr,
                    geboorteDatum: geboorteDatum,
                    postcode: postcode,
                    stad: stad,
                    land: land,
                    gsm_nr: gsm_nr,
                    actief: actief ?? true,
                    rol: rol,
                },
            })
        ).id;
    } catch (e) {
        throw new ServiceError(textCodes.DUPLICATE, 400);
    }

    if (id === null || id == undefined) {
        throw new ServiceError(textCodes.INVALIDDATA, 400);
    }

    let token = jwtUse.generateJWT(id, expiresInSeconds);
    return token;
};

// Login
const login = async ({ email, password }: { email: string; password: string }): Promise<string> => {
    let result = await data.prisma.user.findUnique({ where: { email: email.toLowerCase() } });

    if (result === null) {
        // user not found

        throw new ServiceError(textCodes.NOUSERFOUND, 404);
    } else if (await argonPassword.verifyPassword(password, result.hashed_password)) {
        return jwtUse.generateJWT(result.id); // user found and password is correct, return a token.
    } else {
        return textCodes.WRONGPASSWORD; // user found but password is incorrect.
    }
};

const updateUser = async (user_id: number, { voornaam, achternaam, email, password, straat, huis_nr, geboorteDatum, postcode, stad, land, gsm_nr, actief, rol }: Partial<User>): Promise<1 | null> => {
    try {
        await data.prisma.user.update({
            where: { id: user_id },
            data: {
                voornaam,
                achternaam,
                email,
                hashed_password: password ? await argonPassword.hashPassword(password) : undefined,
                geboorteDatum,
                straat,
                huis_nr,
                postcode,
                stad,
                land,
                gsm_nr,
                actief,
                rol,
            },
        });
        return 1;
    } catch (e: any) {
        // see https://www.prisma.io/docs/orm/reference/error-reference#error-codes for P2002
        if (e.code === "P2002") {
            throw new ServiceError(textCodes.EMAILALREADYEXISTS, 405);
        }
        throw new ServiceError(textCodes.INVALIDDATA, 404);
    }
};

const find = async (user_id: number): Promise<PasswordlessUser | null> => {
    const result = (await data.prisma.user.findUnique({ where: { id: user_id } })) as DBUser;

    if (result === null) {
        throw new ServiceError(textCodes.NOUSERFOUND, 404);
    }

    let passwordlessUser = {
        id: result.id,
        voornaam: result.voornaam,
        achternaam: result.achternaam,
        email: result.email,
        straat: result.straat,
        huis_nr: result.huis_nr,
        geboorteDatum: result.geboorteDatum,
        postcode: result.postcode,
        stad: result.stad,
        land: result.land,
        gsm_nr: result.gsm_nr ?? undefined,
        actief: result.actief,
        rol: result.rol,
    };

    return passwordlessUser;
};

const deleteUser = async (user_id: number): Promise<number> => {
    try {
        await data.prisma.user.delete({ where: { id: user_id } });
    } catch (e) {
        throw new ServiceError(textCodes.USERMISSING, 404);
    }

    return 1;
};
export default {
    create,
    login,
    updateUser,
    find,
    deleteUser,
};
