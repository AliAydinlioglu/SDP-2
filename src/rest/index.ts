import Router from "@koa/router";
import Application from "koa";
import user from "./user";
import health from "./health";
import auth from "./auth";
import endpoints from "../constants/endpoints";
import site from "./site";
import melding from "./melding";
import machine from "./machine";
import onderhoud from "./onderhoud";

// From this file we will further install other routers to various endpoints.
// This is the main entry point for the REST API.
const installRest = (app: Application) => {
    // create a router for the /api endpoint
    const router = new Router({
        prefix: endpoints.apiPrefix,
    });

    router.use(async (ctx, next) => {
        await next();
    });

    router.get("/", async (ctx) => {
        ctx.body = { message: "API" };
    });

    // Create nested routers for various endpoints
    health.installRouter(router); // install the health router
    user.installRouter(router); // install the user router
    auth.installRouter(router); // install the auth router
    site.installRouter(router); // install the site router
    melding.installRouter(router); // install the melding
    machine.installRouter(router); // install the machine router
    onderhoud.installRouter(router); // install the onderhoud router

    // add the router to the koa app
    app.use(router.routes()).use(router.allowedMethods());
};

export default { installRest };
