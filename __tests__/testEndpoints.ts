import endpoints from "../src/constants/endpoints";

let base = endpoints.apiPrefix + endpoints.userPrefix;
let healthBase = endpoints.apiPrefix + endpoints.health;

//Endpoints
// User
let apiUserFolder = base + endpoints.userPostFolderEndpoint; // /api/users/folders
let apiUserFolderID = base + endpoints.userFolderEndpoint; // /api/users/folders/:folderID
let apiUserFolderCard = base + endpoints.userPostCardEndpoint; // /api/users/folders/:folderID/cards
let apiUserFolderCardID = base + endpoints.userCardEndpoint; // /api/users/folders/:folderID/cards/:cardID
let apiHealthPing = healthBase + endpoints.ping; // /api/health/ping
let apiHealthVersion = healthBase + endpoints.version; // /api/health/version

// Public Folder
let apiPublicFolder = endpoints.apiPrefix + endpoints.publicFolderPrefix;
let apiPublicCard = endpoints.apiPrefix + endpoints.userPostCardEndpoint; // /api/folders/:folderID/cards
let apiPublicCardID = endpoints.apiPrefix + endpoints.userCardEndpoint; // /api/folders/:folderID/cards/:cardID
let apiPublicCardScore = endpoints.apiPrefix + endpoints.userScoreEndpoint; // /api/folders/:folderID/cards/:cardID/scores

// cardoverview
let apiCardOverview = base + endpoints.userGetCardoverviewEndpoint; // /api/users/cardoverviews/:cardID

export default {
    apiUserFolder,
    apiUserFolderID,
    apiUserFolderCard,
    apiUserFolderCardID,
    apiHealthPing,
    apiHealthVersion,
    apiPublicFolder,
    apiPublicCard,
    apiPublicCardID,
    apiPublicCardScore,
    apiCardOverview,
};
