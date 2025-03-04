import Router from "@koa/router";
import user from "../service/user";
import { Context } from "koa";
import Joi from "joi";
import validation from "../core/validation";
import {Rol} from "@prisma/client";

const createUser = {
    execute: async (ctx: Context) => {
        const { voornaam, achternaam, email, password, gsm_nr, huis_nr, straat, stad, postcode, land, rol, actief } = ctx.request.body as {
            voornaam: string;
            achternaam: string;
            email: string;
            password: string;
            gsm_nr?: string;
            huis_nr: string;
            straat: string;
            stad: string;
            postcode: string;
            land: string;
            rol?: string;
            actief?: boolean;
        };

        let token = await user.create({
            voornaam,
            achternaam,
            email,
            password,
            gsm_nr,
            huis_nr,
            straat,
            stad,
            postcode,
            land,
            rol: rol as Rol,
            actief,
        });

        ctx.set("Authorization", `Bearer ${token}`);
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
            straat: Joi.string().max(255).required(),
            stad: Joi.string().max(255).required(),
            postcode: Joi.string().max(127).required(),
            land: Joi.string().max(127).required(),
            rol: Joi.string().valid(...Object.values(Rol)).optional(),
            actief: Joi.boolean().optional().default(true),
        }),
    },
};

const loginUser = {
    execute: async (ctx: Context) => {
        const { email, password } = ctx.request.body as { email: string; password: string };

        const token = await user.login({ email, password });
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

    router.post("/register", validation.validateSchema(createUser.schema), createUser.execute);
    router.post("/login", validation.validateSchema(loginUser.schema), loginUser.execute);

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
