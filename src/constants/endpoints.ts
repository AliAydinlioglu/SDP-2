let api = "api";
let user = "users";
let site = "sites";

// index.ts
let apiPrefix = "/" + api;

// Health
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
let userSelfEndpoint = "/me";

// site.ts
//// Root
let sitePrefix = "/" + site;
//// Site Endpoints
let siteSiteEndpoint = "/"; // Get all sites, create a new site
let siteDetailEndpoint = "/:id"; // Get, update, or delete a specific site

export default {
    apiPrefix,

    userPrefix,
    userUserEndpoint,
    userSelfEndpoint,

    sitePrefix,
    siteSiteEndpoint,
    siteDetailEndpoint,

    health,
    ping,
    version,
};
