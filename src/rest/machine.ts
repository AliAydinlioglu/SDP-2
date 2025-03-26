import { Context } from "koa";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";
import permissionCheck from "../core/CRUDPerms";
import machineService from "../service/machine";

// Create a new machine (Allowed for ADMIN, MANAGER and TECHNIEKER)
const createMachine = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);
        const machine = ctx.request.body;
        const result = await machineService.create(machine);
        ctx.body = result;
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            site_id: Joi.number().required(),
            locatie: Joi.string().required(),
            info: Joi.string().required(),
            status: Joi.string().required(),
            prod_status: Joi.string().required(),
            uptime: Joi.number().required(),
            technieker_id: Joi.number().required(),
            dagenSindsOnderhoud: Joi.number().required(),
            volgendOnderhoud: Joi.date().required(),
        }),
    },
};

// Get all machines (Allowed for ADMIN, MANAGER and TECHNIEKER)
const getAllMachines = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);
    const machines = await machineService.findAll();
    ctx.body = machines;
    ctx.status = 200;
};

// Get a specific machine (Allowed for ADMIN, MANAGER and TECHNIEKER)
const getMachine = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);
    const machine_id = parseInt(ctx.params.id, 10);
    const machine = await machineService.find(machine_id);
    ctx.body = machine;
    ctx.status = 200;
};

// Update a machine (Allowed for ADMIN, MANAGER and TECHNIEKER)
const updateMachine = {
    execute: async (ctx: Context) => {
        await permissionCheck([Rol.ADMINISTRATOR, Rol.MANAGER, Rol.TECHNIEKER], ctx.user_id);
        const machine_id = parseInt(ctx.params.id, 10);
        const updateData = ctx.request.body;
        const result = await machineService.updateMachine(machine_id, updateData);
        ctx.body = result;
        ctx.status = 200;
    },
    schema: {
        body: Joi.object({
            site_id: Joi.number(),
            locatie: Joi.string(),
            info: Joi.string(),
            status: Joi.string(),
            prod_status: Joi.string(),
            uptime: Joi.number(),
            technieker_id: Joi.number(),
            dagenSindsOnderhoud: Joi.number(),
            volgendOnderhoud: Joi.date(),
        }).min(1),
    },
};

// Delete a machine (Allowed for ADMIN only)
const deleteMachine = async (ctx: Context) => {
    await permissionCheck([Rol.ADMINISTRATOR], ctx.user_id);
    const machine_id = parseInt(ctx.params.id, 10);
    await machineService.deleteMachine(machine_id);
    ctx.body = { message: `Machine with id: ${machine_id} deleted successfully` };
    ctx.status = 200;
};

// Install the router
const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.machinePrefix,
    });

    router.use(validation.validateSchema(validation.headerAuthorizationSchema));

    // Machine collection routes
    router.get("/", getAllMachines); // GET /machines
    router.post("/", validation.validateSchema(createMachine.schema), createMachine.execute); // POST /machines

    // Machine detail routes
    router.get("/:id", getMachine); // GET /machines/:id
    router.put("/:id", validation.validateSchema(updateMachine.schema), updateMachine.execute); // PUT /machines/:id
    router.delete("/:id", deleteMachine); // DELETE /machines/:id

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
