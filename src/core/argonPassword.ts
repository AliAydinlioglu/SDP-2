import config from "config";
import argon2 from "argon2";
import logging from "./logging";

const ARGON_SALT_LENGTH: number = config.get("auth.argon.saltLength");
const ARGON_HASH_LENGTH: number = config.get("auth.argon.hashLength");
const ARGON_TIME_COST: number = config.get("auth.argon.timeCost");
const ARGON_MEMORY_COST: number = config.get("auth.argon.memoryCost");

const hashPassword = async (password: string) => {
    const passwordHash = await argon2.hash(password, {
        type: argon2.argon2id,
        saltLength: ARGON_SALT_LENGTH,
        hashLength: ARGON_HASH_LENGTH,
        timeCost: ARGON_TIME_COST,
        memoryCost: ARGON_MEMORY_COST,
    });

    return passwordHash;
};

const verifyPassword = async (password: string, passwordHash: string) => {
    try {
        const check = await argon2.verify(passwordHash, password, {
            type: argon2.argon2id,
            saltLength: ARGON_SALT_LENGTH,
            hashLength: ARGON_HASH_LENGTH,
            timeCost: ARGON_TIME_COST,
            memoryCost: ARGON_MEMORY_COST,
        });

        return check;
    } catch (err) {
        logging.getLogger().error(err);
        return false;
    }
};

export default {
    hashPassword,
    verifyPassword,
};
