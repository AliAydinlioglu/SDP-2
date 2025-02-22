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
        const { name, email, password } = ctx.request.body as { name?: string; email?: string; password?: string };

        await userService.updateUser(ctx.user_id, { name, email, password });

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            name: Joi.string().optional(),
            email: Joi.string().email().optional(),
            password: Joi.string().optional(),
        }).or("name", "email", "password"),
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
