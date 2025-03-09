import Router from "@koa/router";
import Application from "koa";
import user from "./user";
import health from "./health";
import auth from "./auth";
import endpoints from "../constants/endpoints";

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

    // add the router to the koa app
    app.use(router.routes()).use(router.allowedMethods());
};

export default { installRest };
