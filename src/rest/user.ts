import { Context } from "koa";
import userService from "../service/user";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import Rol from "../types/rol";

const getUser = async (ctx: Context) => {
    ctx.body = await userService.find(ctx.user_id);
    ctx.status = 200;
};

const updateUser = {
    execute: async (ctx: Context) => {
        const { voornaam, achternaam, email, password, straat, huis_nr, postcode, stad, land, gsm_nr, actief, rol } = ctx.request.body as {
            voornaam?: string;
            achternaam?: string;
            straat?: string;
            huis_nr?: string;
            postcode?: string;
            stad?: string;
            land?: string;
            gsm_nr?: string;
            actief?: boolean;
            email?: string;
            password?: string;
            rol?: Rol;
        };

        await userService.updateUser(ctx.user_id, {
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

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            voornaam: Joi.string().optional(),
            achternaam: Joi.string().optional(),
            email: Joi.string().email().optional(),
            password: Joi.string().optional(),
            straat: Joi.string().optional(),
            huis_nr: Joi.string().optional(),
            postcode: Joi.string()
                .pattern(/^\d{4,5}$/)
                .optional(),
            stad: Joi.string().optional(),
            land: Joi.string().optional(),
            gsm_nr: Joi.string().optional(),
            actief: Joi.boolean().optional(),
            rol: Joi.object().optional(),
        }).or("voornaam", "achternaam", "email", "password", "straat", "huis_nr", "postcode", "stad", "land", "gsm_nr", "actief", "rol"),
    },
};

const deleteUser = async (ctx: Context) => {
    let result = await userService.deleteUser(ctx.user_id);

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
