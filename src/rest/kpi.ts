import { Context } from "koa";
import Router from "@koa/router";
import endpoints from "../constants/endpoints";
import kpiService from "../service/kpi";

const getKPIs = async (ctx: Context) => {
    const kpiData = await kpiService.getKPIs();
    ctx.status = 200;
    ctx.body = kpiData;
};

const installRouter = (parentRouter: Router) => {
    const router = new Router({
        prefix: endpoints.kpiPrefix,
    });

    router.get("/", getKPIs);

    parentRouter.use(router.routes()).use(router.allowedMethods());
};

export default { installRouter };
