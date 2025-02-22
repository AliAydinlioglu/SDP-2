const settings = {
    env: "",
    log: {
        level: "silly", // "error", "warn", "info", "verbose", "debug", "silly"
        disabled: false,
    },
    cors: {
        origins: ["http://localhost:9000"],
        maxAge: 3 * 60 * 60,
    },
    database: {
        client: "mysql2",
        host: "",
        port: 0,
        name: "", // schema name
        username: "",
        password: "", // password is set in .env file
    },
    auth: {
        argon: {
            saltLength: 16,
            hashLength: 32,
            timeCost: 6,
            memoryCost: 2 ** 17,
        },
        jwt: {
            secret: "",
        },
    },
    port: 0,
};

export default settings;
