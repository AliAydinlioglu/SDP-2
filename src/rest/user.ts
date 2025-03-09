import { Context } from "koa";
import userService from "../service/user";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";

const getUser = async (ctx: Context) => {
    ctx.body = await userService.find(ctx.user_id);
    ctx.status = 200;
};

const updateUser = {
    execute: async (ctx: Context) => {
        const user = ctx.request.body as {
            voornaam: string;
            achternaam: string;
            email: string;
            gsm_nr?: string;
            geboorteDatum: Date;
            straat: string;
            huis_nr: string;
            stad: string;
            postcode: string;
            land: string;
            password: string;
            actief: boolean;
            rol: Rol;
        };

        await userService.updateUser(ctx.user_id, user);

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            voornaam: Joi.string(),
            achternaam: Joi.string(),
            email: Joi.string().email(),
            password: Joi.string(),
            straat: Joi.string(),
            huis_nr: Joi.string(),
            geboorteDatum: Joi.date(),
            postcode: Joi.string().pattern(/^\d{4,5}$/),
            stad: Joi.string(),
            land: Joi.string(),
            gsm_nr: Joi.string().optional(),
            actief: Joi.boolean(),
            rol: Joi.object(),
        }).min(1),
    },
};

const deleteUser = async (ctx: Context) => {
    await userService.deleteUser(ctx.user_id);

    ctx.status = 200;
    ctx.body = { message: "User deleted" };
};

const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.userPrefix,
    });

    router.use(validation.validateSchema(validation.headerAuthorizationSchema));

    router.get(endpoints.userUserEndpoint, getUser);
    router.put(endpoints.userUserEndpoint, validation.validateSchema(updateUser.schema), updateUser.execute);
    router.delete(endpoints.userUserEndpoint, deleteUser);

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
