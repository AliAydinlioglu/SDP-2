import endpoints from "../src/constants/endpoints";

let base = endpoints.apiPrefix + endpoints.userPrefix;
let healthBase = endpoints.apiPrefix + endpoints.health;

//Endpoints
// User
let apiHealthPing = healthBase + endpoints.ping; // /api/health/ping
let apiHealthVersion = healthBase + endpoints.version; // /api/health/version


export default {
    apiHealthPing,
    apiHealthVersion,
};
