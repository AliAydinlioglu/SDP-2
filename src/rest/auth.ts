import Router from "@koa/router";
import userService from "../service/user";
import { Context } from "koa";
import Joi from "joi";
import validation from "../core/validation";
import { Rol } from "@prisma/client";

const createUser = {
    execute: async (ctx: Context) => {
        const user = ctx.request.body as {
            voornaam: string;
            achternaam: string;
            email: string;
            password: string;
            gsm_nr?: string;
            geboorteDatum: Date;
            huis_nr: string;
            straat: string;
            stad: string;
            postcode: string;
            land: string;
            rol: Rol;
            actief: boolean;
        };

        let token = await userService.create(user);

        ctx.body = { token };
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            voornaam: Joi.string().max(127).required(),
            achternaam: Joi.string().max(127).required(),
            email: Joi.string().email().max(255).required(),
            password: Joi.string().min(8).required(),
            gsm_nr: Joi.string().max(127).optional(),
            huis_nr: Joi.string().max(10).required(),
            geboorteDatum: Joi.date().required(),
            straat: Joi.string().max(255).required(),
            stad: Joi.string().max(255).required(),
            postcode: Joi.string().max(127).required(),
            land: Joi.string().max(127).required(),
            rol: Joi.string().valid(...Object.values(Rol)),
            actief: Joi.boolean().default(true),
        }),
    },
};

const loginUser = {
    execute: async (ctx: Context) => {
        const { email, password } = ctx.request.body as { email: string; password: string };

        const token = await userService.login({ email, password });
        // ctx.set("Authorization", `Bearer ${token}`);
        ctx.body = { token };
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

    router.post("/register", validation.validateSchema(createUser.schema), createUser.execute);
    router.post("/login", validation.validateSchema(loginUser.schema), loginUser.execute);

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
