import { Context } from "koa";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";
import permissionCheck from "../core/CRUDPerms";
import onderhoudService from "../service/onderhoud";

// Create a new onderhoud record
const createOnderhoud = {
    execute: async (ctx: Context) => {
        // Only administrators, managers and technicians can create onderhoud records
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);

        const onderhoud = ctx.request.body;
        const result = await onderhoudService.create(onderhoud);

        ctx.body = result;
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            datum: Joi.date().required(),
            startTijd: Joi.date().required(),
            eindTijd: Joi.date().required(),
            technieker_id: Joi.number().required(),
            reden: Joi.string().required(),
            rapport: Joi.any().required(), // Accept any JSON value
            opmerkingen: Joi.string().required(),
            status: Joi.string().required(),
            machine_id: Joi.number().required(),
        }),
    },
};

// Get all onderhoud records
const getAllOnderhoud = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);

    const onderhouds = await onderhoudService.findAll();
    ctx.body = onderhouds;
    ctx.status = 200;
};

// Get a specific onderhoud record
const getOnderhoud = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);

    const onderhoud_id = parseInt(ctx.params.id, 10);
    const onderhoud = await onderhoudService.find(onderhoud_id);
    ctx.body = onderhoud;
    ctx.status = 200;
};

// Update an onderhoud record
const updateOnderhoud = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);

        const onderhoud_id = parseInt(ctx.params.id, 10);
        const updateData = ctx.request.body;
        const result = await onderhoudService.updateOnderhoud(onderhoud_id, updateData);
        ctx.body = result;
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            datum: Joi.date(),
            startTijd: Joi.date(),
            eindTijd: Joi.date(),
            technieker_id: Joi.number(),
            reden: Joi.string(),
            rapport: Joi.any(),
            opmerkingen: Joi.string(),
            status: Joi.string(),
            machine_id: Joi.number(),
        }).min(1),
    },
};

// Delete an onderhoud record
const deleteOnderhoud = async (ctx: Context) => {
    // Only administrators can delete onderhoud records
    await permissionCheck([Rol.ADMINISTRATOR], ctx.user_id);

    const onderhoud_id = parseInt(ctx.params.id, 10);
    await onderhoudService.deleteOnderhoud(onderhoud_id);

    ctx.body = { message: `Onderhoud record with id: ${onderhoud_id} deleted successfully` };
    ctx.status = 200;
};

// Install the router
const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.onderhoudPrefix, // Ensure this prefix is defined in your endpoints
    });

    router.use(validation.validateSchema(validation.headerAuthorizationSchema));

    // Onderhoud collection routes
    router.get("/", getAllOnderhoud); // GET /onderhoud
    router.post("/", validation.validateSchema(createOnderhoud.schema), createOnderhoud.execute); // POST /onderhoud

    // Onderhoud detail routes
    router.get("/:id", getOnderhoud); // GET /onderhoud/:id
    router.put("/:id", validation.validateSchema(updateOnderhoud.schema), updateOnderhoud.execute); // PUT /onderhoud/:id
    router.delete("/:id", deleteOnderhoud); // DELETE /onderhoud/:id

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
