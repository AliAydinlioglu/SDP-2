#!/bin/sh
set -e

echo "Waiting for database to be ready..."
node -e '
const net = require("net");
const host = process.env.DATABASE_HOST || "mysql";
const port = parseInt(process.env.DATABASE_PORT || "3306", 10);
let retries = 30;

function check() {
  const socket = new net.Socket();
  socket.setTimeout(2000);
  socket.on("connect", () => {
    socket.destroy();
    console.log("Database port is reachable!");
    process.exit(0);
  });
  socket.on("error", () => {
    socket.destroy();
    retries--;
    if (retries <= 0) {
      console.error("Database connection timed out");
      process.exit(1);
    }
    setTimeout(check, 1000);
  });
  socket.on("timeout", () => {
    socket.destroy();
    retries--;
    if (retries <= 0) {
      console.error("Database connection timed out");
      process.exit(1);
    }
    setTimeout(check, 1000);
  });
  socket.connect(port, host);
}
check();
'

echo "Pushing Prisma schema to database..."
npx prisma db push --skip-generate

echo "Checking if database needs seeding..."
node -e '
const { PrismaClient } = require("@prisma/client");
const prisma = new PrismaClient();
prisma.user.count().then(count => {
  if (count === 0) {
    console.log("Database is empty. Running seed...");
    require("child_process").execSync("npx ts-node ./src/data/seeds/seed.ts", { stdio: "inherit" });
  } else {
    console.log(`Database already has ${count} users, skipping seed.`);
  }
  return prisma.$disconnect();
}).catch(err => {
  console.warn("Seed check note:", err.message);
});
' || true

echo "Starting backend server..."
exec "$@"
