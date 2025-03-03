let api = "api";
let user = "users";

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



export default {

    apiPrefix,
    userPrefix,

    userUserEndpoint,

    health,
    ping,
    version,
};
