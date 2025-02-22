import knex from "knex";
import logging from "../core/logging";
import config from "config";
import path from "path";
import EMPTYDATABASE from "./resetMySQL";

const NODE_ENV = config.get("env");
const isDevelopment = NODE_ENV === "development";

const DATABASE_CLIENT = config.get("database.client");
const DATABASE_NAME = config.get("database.name"); // schema name
const DATABASE_HOST = config.get("database.host");
const DATABASE_PORT = config.get("database.port");
const DATABASE_USERNAME = config.get("database.username");
const DATABASE_PASSWORD = config.get("database.password");

let RESETDB = false;

if (NODE_ENV === "test") {
    RESETDB = true;
}

let knexInstance: knex.Knex | null = null;

const getKnex = () => {
    if (!knexInstance) {
        throw new Error("Please initialize the data layer before getting the Knex instance");
    }
    return knexInstance;
};

// Entry point for the DB setup.
const initializeData = async () => {
    const logger = logging.getLogger();
    logger.info("Initializing connection to the database");

    const customStringIndex = process.argv.indexOf("--custom-string");

    if (customStringIndex !== -1) {
        const customStringValue = process.argv[customStringIndex + 1];
        if (customStringValue == "reset") {
            RESETDB = true;
            logger.warn("Reset is enabled.");
        }
    }

    // OPTIONS
    const knexOptions = {
        client: DATABASE_CLIENT as string,
        connection: {
            host: DATABASE_HOST as string,
            port: DATABASE_PORT as number,
            // database: DATABASE_NAME as string,
            database: null as string | null, // We'll set the database name later. We want the driver to ignore the database name for now and thus we use null instead of an empty string.
            user: DATABASE_USERNAME as string,
            password: DATABASE_PASSWORD as string,
            insecureAuth: isDevelopment,
        },
        // debug: isDevelopment,
        debug: false,
        migrations: {
            tableName: "knex_meta", // The name of the table that knex will create inside your schema to keep track of the migrations.
            directory: path.join(__dirname, "migrations"), // The path to the directory that contains the migration files.
        },
        seeds: {
            directory: path.join(__dirname, "seeds"),
        },
    };
    knexInstance = knex(knexOptions); // Initial connection to the database
    // OPTIONS

    try {
        await knexInstance.raw("SELECT 1+1 AS result"); // test the connection
        logger.info("Successfully connected to the database 1/2");

        await createSchemaIfNull();

        if (RESETDB) {
            logger.warn("Resetting the database.");
            await EMPTYDATABASE();
            RESETDB = false;
            logger.warn("Resetting is done, continuing with startup.");
        }

        await runMigrations();
        await runSeeds();

        // Test the one last time connection
        await knexInstance.raw("SELECT 1+1 AS result");
        logger.info("Successfully connected to the database 2/2");
    } catch (error: unknown) {
        if (error instanceof Error) {
            logging.getLogger().error(error.message, { error });
            throw new Error("Could not initialize the data layer. (You most likely need to create the databse)");
        } else {
            throw new Error("Unkown error type, could not initialize the data layer");
        }
    }

    async function createSchemaIfNull() {
        // Create the database if it doesn't exist
        if (knexInstance === null) {
            throw new Error("Knex instance is null, please initialize the data layer");
        }

        await knexInstance.raw(`CREATE DATABASE IF NOT EXISTS ${DATABASE_NAME}`); // create the database / schema if it doesn't exist
        await knexInstance.destroy(); // knex is not connected with the right database name, so we need to destroy the connection and create a new one.
        knexOptions.connection.database = DATABASE_NAME as string; // Add the name of schema that we just created or already exists to the options.
        // Last connection to the database
        knexInstance = knex(knexOptions);
    }

    async function runMigrations() {
        // Database ready, run migrations
        if (knexInstance === null) {
            throw new Error("Knex instance is null, please initialize the data layer");
        }

        try {
            await knexInstance.migrate.latest(); // .latest() will only run migrations that have not yet been run (that are not in the migration table). If you want to rollback and re-run all of your migrations, you can use the rollback command. .forceLatest() will ignore the current migration table and rerun all migrations.
            logger.info("Successfully migrated the database");
        } catch (error) {
            logger.error("Error while migrating the database", {
                error,
            });
            throw new Error("Migrations failed, check the logs");
        }
    }

    async function runSeeds() {
        // Seed the database with initial data
        if (knexInstance === null) {
            throw new Error("Knex instance is null, please initialize the data layer");
        }

        if (isDevelopment) {
            try {
                await knexInstance.seed.run();
                logger.info("Successfully seeded the database");
            } catch (error) {
                logger.error("Error while seeding database", {
                    error,
                });
            }
        }
    }
};

// The Object.freeze() method creates an immutable object. tables is just an object, but we won't be able to change its properties.
const tables = Object.freeze({
    card: "card",
    folder: "folder",
    scoreboard: "scoreboard",
    user: "user",
});

const shutdownData = async () => {
    if (knexInstance === null) {
        throw new Error("Knex instance is null, please initialize the data layer");
    }

    const logger = logging.getLogger();

    logger.info("Shutting down database connection");

    await knexInstance.destroy();
    knexInstance = null;

    logger.info("Database connection closed");
};

export default {
    initializeData,
    getKnex,
    tables,
    shutdownData,
};
