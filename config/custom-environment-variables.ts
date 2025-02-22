const settings = {
    env: "NODE_ENV",
    PORT: "PORT",
    database: {
        host: "DATABASE_HOST",
        port: "DATABASE_PORT",
        name: "DATABASE_NAME", // schema name
        username: "DATABASE_USERNAME",
        password: "DATABASE_PASSWORD",
    },
    auth: {
        jwt: {
            secret: "JWTSECRET",
        },
    },
};
export default settings;
