// import package.json as a whole, the file is 2 folders up from this file.
import packageJson from "../../package.json";

const ping = (): { pong: true } => {
    return { pong: true };
};

const getVersion = (): { name: string; version: string; env: string | undefined } => {
    return {
        name: packageJson.name,
        version: packageJson.version,
        env: process.env.NODE_ENV,
    };
};

export default { ping, getVersion };
