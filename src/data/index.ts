// src/data/index.ts
import { PrismaClient } from "@prisma/client";
import logging from "../core/logging";

const prisma = new PrismaClient();

async function initializeData(): Promise<void> {
    logging.getLogger().info("Initializing connection to the database");

    await prisma.$connect();

    logging.getLogger().info("Successfully connected to the database");
}

async function shutdownData(): Promise<void> {
    logging.getLogger().info("Shutting down database connection");

    await prisma?.$disconnect();

    logging.getLogger().info("Database connection closed");
}

export default { initializeData, shutdownData, prisma };
