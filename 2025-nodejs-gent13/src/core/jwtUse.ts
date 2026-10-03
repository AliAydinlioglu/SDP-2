import config from "config";
import jwt from "jsonwebtoken";
import { ServiceError } from "./errorHandler";
import textCodes from "../constants/textCodes";

const JWTSECRET: string = config.get("auth.jwt.secret");

const generateJWT = (user_id: number, expiresInSeconds?: number) => {
    let options: jwt.SignOptions = {
        expiresIn: "1h",
    };

    if (expiresInSeconds !== undefined) {
        options.expiresIn = expiresInSeconds;
    }

    const payload = {
        user_id: user_id, // user_id is on auto increment, so it is guaranteed to be unique and an already assigned id won't be used ever again by MySQL.
    };

    const jwtToken = jwt.sign(payload, JWTSECRET, options);

    return jwtToken;
};

// jwt.verify also checks for the expiration time.
const decodeVerifyJWT = (jwtToken: string) => {
    try {
        // Here we actually do VALIDATE the jwt, since the signature itself is created using the data (user id + expiration date) it is impossible to temper with it.
        const checked = jwt.verify(jwtToken, JWTSECRET);

        return checked;
    } catch (err) {
        return false;
    }
};

// Every database update across the platform is associated with a specific user account. This necessitates a user ID, which we obtain from a JWT through the getUserID function. Essentially, to make any modification in the database, we invariably need a user ID derived from a JWT.
const getUserID = (jwtToken: string) => {
    const decoded = decodeVerifyJWT(jwtToken); // decoding = verifying

    if (typeof decoded === "string" || decoded === false) {
        throw new ServiceError(textCodes.INVALIDJWT, 401);
    } else {
        return decoded.user_id;
    }
};

export default {
    generateJWT,
    decodeVerifyJWT,
    getUserID,
};
