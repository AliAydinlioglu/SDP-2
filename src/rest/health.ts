import { Context } from "koa";
import healthService from "../service/health";
import Router from "@koa/router";
import endpoints from "../constants/endpoints";

const ping = async (ctx: Context) => {
    ctx.status = 200;
    ctx.body = healthService.ping();
};

const getVersion = async (ctx: Context) => {
    ctx.status = 200;
    ctx.body = healthService.getVersion();
};

const installRouter = (parentRouter: Router) => {
    // create a router for the /api/data endpoint
    const router = new Router({
        prefix: endpoints.health,
    });

    router.get("/ping", ping); // GET .../api/health/ping
    router.get("/version", getVersion); // GET .../api/health/version

    // add the router to the parent router
    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
