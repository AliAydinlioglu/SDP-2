let api = "api";
let user = "users";
let site = "sites";

// index.ts
let apiPrefix = "/" + api;

// Health"
let health = "/" + "health";
let ping = "/" + "ping";
let version = "/" + "version";

// ENDPOINTS
// Schema: filename + protocol + property name + "endpoint"
// Only mention the protocol if the endpoint is unique

// user.ts
//// Root
let userPrefix = "/" + user;
//// User
let userUserEndpoint = "/";

//site.ts
////Site
let sitePrefix = "/" + site;

export default {
    apiPrefix,
    userPrefix,
    sitePrefix,

    userUserEndpoint,

    health,
    ping,
    version,
};
