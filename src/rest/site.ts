import { Context } from "koa";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";
import permissionCheck from "../core/CRUDPerms";
import siteService from "../service/site";

const createSite = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR], ctx.user_id);

        const site = ctx.request.body as {
            naam: string;
            verantw_id: number;
        };

        let id = await siteService.create(site);

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            naam: Joi.string().max(127).required(),
            verantw_id: Joi.number().required(),
        }),
    },
};

const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.sitePrefix,
    });

    router.use(validation.validateSchema(validation.headerAuthorizationSchema));

    router.post("/", validation.validateSchema(createSite.schema), createSite.execute);

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
