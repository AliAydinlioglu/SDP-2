import Koa from "koa";
import koaCors from "@koa/cors";
import config from "config";
import koaBody from "koa-body";
import koaHelmet from "koa-helmet";

/**
 * Install all required middlewares in the given app.
 *
 * @param {koa.Application} koa - The Koa application.
 */
const installMiddleware = (koa: Koa) => {
    const CORS_ORIGINS = config.get("cors.origins") as string[];
    const CORS_MAX_AGE = config.get("cors.maxAge") as number;

    koa.use(koaBody({ multipart: true })); // Allows us to use ctx.request.body
    koa.use(koaHelmet()); // Adds security headers

    koa.use(
        koaCors({
            origin: (ctx) => {
                if (ctx.request.header.origin !== undefined && CORS_ORIGINS.indexOf(ctx.request.header.origin) !== -1) {
                    return ctx.request.header.origin;
                }
                return CORS_ORIGINS[0]; // Not a valid domain at this point, let's return the first valid as we should return a string
            },
            allowHeaders: ["Accept", "Content-Type", "Authorization"],
            maxAge: CORS_MAX_AGE,
        })
    );
};

export default installMiddleware;
