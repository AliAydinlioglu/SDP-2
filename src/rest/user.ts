import { Context } from "koa";
import userService from "../service/user";
import Router from "@koa/router";
import textCodes from "../constants/textCodes";
import Joi from "joi";
import validation from "../core/validation";
import parameters from "../core/parameters";
import endpoints from "../constants/endpoints";

const getUser = async (ctx: Context) => {
    ctx.body = await userService.find(ctx.user_id);
    ctx.status = 200;
};

const updateUser = {
    execute: async (ctx: Context) => {
        const {voornaam, achternaam, email, password, adres, gsm, actief}
         = ctx.request.body as { voornaam?: string; achternaam?: string; adres?: string; gsm?: string; actief?: boolean; email?: string; password?: string };

        await userService.updateUser(ctx.user_id, { voornaam, achternaam, email, password, adres, gsm, actief  });

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
          voornaam: Joi.string().optional(),
          achternaam: Joi.string().optional(),
          email: Joi.string().email().optional(),
          password: Joi.string().optional(),
          adres: Joi.string().optional(),
          gsm: Joi.string().optional(),
          actief: Joi.boolean().optional(),
        }).or("voornaam", "achternaam", "email", "password", "adres", "gsm", "actief"),
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
