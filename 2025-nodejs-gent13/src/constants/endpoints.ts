let api = "api";
let user = "users";
let site = "sites";
let machine = "machines";
let melding = "meldingen";
let onderhoud = "onderhoud"; // new onderhoud endpoints
let kpi = "kpis"; // new KPI endpoints

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

// machine.ts
let machinePrefix = "/" + machine;
let machineMachineEndpoint = "/";
let machineDetailEndpoint = "/:id";

// melding.ts
//// Root
let meldingPrefix = "/" + melding;
//// Melding Endpoints
let meldingMeldingEndpoint = "/"; // Get all meldingen, create a new melding
let meldingDetailEndpoint = "/:id"; // Get, update, or delete a specific melding

// onderhoud.ts
let onderhoudPrefix = "/" + onderhoud;
let onderhoudOnderhoudEndpoint = "/"; // Get all onderhoud records, create a new maintenance record
let onderhoudDetailEndpoint = "/:id"; // Get, update, or delete a specific onderhoud record

// kpi.ts
let kpiPrefix = "/" + kpi; // Get all calculated KPIs

export default {
    apiPrefix,

    userPrefix,
    userUserEndpoint,
    userSelfEndpoint,

    sitePrefix,
    siteSiteEndpoint,
    siteDetailEndpoint,

    machinePrefix,
    machineMachineEndpoint,
    machineDetailEndpoint,

    meldingPrefix,
    meldingMeldingEndpoint,
    meldingDetailEndpoint,

    onderhoudPrefix,
    onderhoudOnderhoudEndpoint,
    onderhoudDetailEndpoint,

    health,
    ping,
    version,
    kpiPrefix, // added KPI endpoint
};
