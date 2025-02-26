import Router from "@koa/router";
import userService from "../service/user";
import { Context } from "koa";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";

const createUser = {
    execute: async (ctx: Context) => {
        const { voornaam, achternaam, email, password, adres, gsm, actief }
         = ctx.request.body as { voornaam: string; achternaam: string; adres: string; gsm?: string; actief?: boolean; email: string; password: string };
 
        let token = await userService.create({ voornaam, achternaam, email, password, adres, gsm, actief });
        ctx.set("Authorization", `Bearer ${token}`);
        ctx.status = 201;
    },
    schema: {
        body: Joi.object({
            voornaam: Joi.string().required(),
            achternaam: Joi.string().required(),
            email: Joi.string().email().required(),
            password: Joi.string().min(8).required(),
            adres: Joi.string().required(),
            gsm: Joi.string().optional(),
            actief: Joi.boolean().optional(),
        }),
    },
};

const loginUser = {
    execute: async (ctx: Context) => {
        const { email, password } = ctx.request.body as { email: string; password: string };

        const token = await userService.login({ email, password });
        ctx.set("Authorization", `Bearer ${token}`);
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            email: Joi.string().email().required(),
            password: Joi.string().required(),
        }).required(),
    },
};

const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: "/auth",
    });

    router.post("/register", validation.validateSchema(createUser.schema), createUser.execute); // POST .../api/data/
    router.post("/login", validation.validateSchema(loginUser.schema), loginUser.execute); // POST .../api/data/

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
