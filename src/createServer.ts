import Koa from "koa";
import rest from "./rest";
import logging from "./core/logging";
import config from "config";
import data from "./data/index";
import installMiddleware from "./core/installMiddlewares";
import { installErrorHandler } from "./core/errorHandler";

const createServer = async () => {
    // LOGGING
    const NODE_ENV = config.get("env"); // env-cmd package will set the NODE_ENV environment for us. We can set it to development, production, staging, etc.
    const LOG_LEVEL = config.get("log.level");
    const LOG_DISABLED = config.get("log.disabled");

    logging.initializeLogger({
        level: LOG_LEVEL as string,
        disabled: LOG_DISABLED as boolean,
        defaultMeta: {
            NODE_ENV,
        },
    });
    // LOGGING

    // DATA
    await data.initializeData();
    // DATA

    // KOA
    const koa = new Koa(); // create a new koa app
    // KOA

    // CORS
    installMiddleware(koa);
    // CORS

    // ERROR HANDLING
    installErrorHandler(koa);
    // ERROR HANDLING

    // REST
    rest.installRest(koa); // add routes to the koa app
    // REST

    const getKoa = () => {
        return koa;
    };

    const start = async () => {
        // actually start the server at given port.
        const port = config.get("PORT");
        koa.listen(port);
        logging.getLogger().info(`Server listening on http://localhost:${port}`);
        logging.getLogger().info(`Check: http://localhost:${port}/api/health/ping`);
    };

    const stop = async () => {
        koa.removeAllListeners();
        await data.shutdownData();
        logging.getLogger().info("Goodbye!");
    };

    return { getKoa, start, stop };
};

export default createServer;
