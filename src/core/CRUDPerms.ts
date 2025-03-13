import { Rol } from "@prisma/client";
import userService from "../service/user";
import { ServiceError } from "./errorHandler";
import textCodes from "../constants/textCodes";
import { Context, Next } from "koa";

const permissionCheck = async (allowedRoles: Rol[], user_id: number) => {
    let user = await userService.find(user_id);

    if (!allowedRoles.includes(user.rol)) {
        throw new ServiceError(textCodes.NOACCESS, 401);
    }
    return true;
};

export default permissionCheck;
