import { Context } from "koa";
import Router from "@koa/router";
import Joi from "joi";
import validation from "../core/validation";
import endpoints from "../constants/endpoints";
import { Rol } from "@prisma/client";
import permissionCheck from "../core/CRUDPerms";
import meldingService from "../service/melding";

// Create a new melding
const createMelding = {
    execute: async (ctx: Context) => {
        // Allow any user type to create a melding
        
        const melding = ctx.request.body as {
            beschrijving: string;
            user_id: number;
            status: string;
            type: string;
            datum: Date;
        };
        
        let id = await meldingService.create(melding);
        
        ctx.status = 200;
        ctx.body = { id };
    },
    schema: {
        body: Joi.object({
            beschrijving: Joi.string().required(),
            user_id: Joi.number().required(),
            status: Joi.string().required(),
            type: Joi.string().required(),
            datum: Joi.date().required(),
        }),
    },
};

// Get all meldingen
const getAllMeldingen = async (ctx: Context) => {
    ctx.body = await meldingService.findAll();
    ctx.status = 200;
};

// Get a specific melding
const getMelding = async (ctx: Context) => {
    const melding_id = parseInt(ctx.params.id, 10);
    ctx.body = await meldingService.find(melding_id);
    ctx.status = 200;
};

// Update a melding
const updateMelding = {
    execute: async (ctx: Context) => {
        const melding_id = parseInt(ctx.params.id, 10);
        await meldingService.update(melding_id, ctx.request.body);
        ctx.status = 200;
        ctx.body = { message: `Melding with id: ${melding_id} updated successfully` };
    },
    schema: {
        body: Joi.object({
            beschrijving: Joi.string().min(1),
            user_id: Joi.number(),
            status: Joi.string(),
            type: Joi.string(),
            datum: Joi.date(),
        }).min(1),
    },
};

// Delete a melding
const deleteMelding = async (ctx: Context) => {
    const melding_id = parseInt(ctx.params.id, 10);
    await meldingService.deleteMelding(melding_id);
    ctx.status = 200;
    ctx.body = { message: `Melding with id: ${melding_id} deleted successfully` };
};

// Install the router
const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.meldingPrefix, // "/meldingen"
    });
    
    router.use(validation.validateSchema(validation.headerAuthorizationSchema));
    
    router.get("/", getAllMeldingen); // GET /meldingen
    router.post("/", validation.validateSchema(createMelding.schema), createMelding.execute); // POST /meldingen
    
    router.get(endpoints.meldingDetailEndpoint, getMelding); // GET /meldingen/:id
    router.put(endpoints.meldingDetailEndpoint, validation.validateSchema(updateMelding.schema), updateMelding.execute); // PUT /meldingen/:id
    router.delete(endpoints.meldingDetailEndpoint, deleteMelding); // DELETE /meldingen/:id
    
    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };