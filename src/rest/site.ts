import { Context } from "koa";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";
import permissionCheck from "../core/CRUDPerms";
import siteService from "../service/site";

// Create a site (Allowed for ADMIN and MANAGER)
const createSite = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER], ctx.user_id);

        const site = ctx.request.body as {
            naam: string;
            verantw_id: number;
        };

        await siteService.create(site);

        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            naam: Joi.string().required(),
            verantw_id: Joi.number().required(),
        }),
    },
};

// Get a specific site (Allowed for ADMIN, MANAGER, and VERANTWOORDELIJKE)
const getSite = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.VERANTWOORDELIJKE], ctx.user_id);
    const site_id = parseInt(ctx.params.id, 10);
    const site = await siteService.find(site_id);
    ctx.body = site;
    ctx.status = 200;
};

// Get all sites (Allowed for ADMIN, MANAGER, and VERANTWOORDELIJKE)
const getAll = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.VERANTWOORDELIJKE], ctx.user_id);
    const sites = await siteService.findAll();
    ctx.body = sites;
    ctx.status = 200;
};

// Update a site (Allowed for ADMIN, MANAGER, and VERANTWOORDELIJKE)
const updateSite = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.VERANTWOORDELIJKE], ctx.user_id);
        const site_id = parseInt(ctx.params.id, 10);
        await siteService.update(site_id, ctx.request.body);
        ctx.status = 200;
        ctx.body = { message: `Site with id: ${site_id} updated successfully` };
    },
    schema: {
        body: Joi.object({
            naam: Joi.string().min(1),
            verantw_id: Joi.number(),
        }).min(1),
    },
};

// Delete a site (Allowed for ADMIN and MANAGER)
const deleteSite = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER], ctx.user_id);
    const site_id = parseInt(ctx.params.id, 10);
    await siteService.deleteSite(site_id);
    ctx.status = 200;
    ctx.body = { message: `Site with id: ${site_id} deleted successfully` };
};

// Install the router
const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.sitePrefix, // "/sites"
    });

    router.use(validation.validateSchema(validation.headerAuthorizationSchema));

    router.get(endpoints.siteSiteEndpoint, getAll); // GET /sites
    router.post(endpoints.siteSiteEndpoint, validation.validateSchema(createSite.schema), createSite.execute); // POST /sites

    router.get(endpoints.siteDetailEndpoint, getSite); // GET /sites/:id
    router.put(endpoints.siteDetailEndpoint, validation.validateSchema(updateSite.schema), updateSite.execute); // PUT /sites/:id
    router.delete(endpoints.siteDetailEndpoint, deleteSite); // DELETE /sites/:id

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
