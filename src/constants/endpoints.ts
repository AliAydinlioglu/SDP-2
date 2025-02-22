let api = "api";
let user = "users";

let folder = "folders";
let folderID = ":folderID";

let card = "cards";
let cardOverview = "cardoverviews";
let cardID = ":cardID";

let score = "scores";
let scoreID = ":scoreID";

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
//// Folder
let userGetFolderEndpoint = "/" + folder + "/" + folderID + "?";
let userPostFolderEndpoint = "/" + folder;
let userFolderEndpoint = "/" + folder + "/" + folderID; // PUT, DELETE
//// Card
let userGetCardEndpoint = "/" + folder + "/" + folderID + "/" + card + "/" + cardID + "?";
let userPostCardEndpoint = "/" + folder + "/" + folderID + "/" + card; // /folder/:folderID/card
let userCardEndpoint = "/" + folder + "/" + folderID + "/" + card + "/" + cardID; // PUT, DELETE
let userGetCardoverviewEndpoint = "/" + cardOverview + "/" + cardID; // GET 
//// Score
let userScoreEndpoint = "/" + folder + "/" + folderID + "/" + card + "/" + cardID + "/" + score;

// publicFolder.ts
//// Root
let publicFolderPrefix = "/" + folder;
let getPublicFolder = "/" + folderID + "?";
let getPublicCard = "/" + folderID + "/" + card + "/" + cardID + "?";
let getPublicCardScore = "/" + folderID + "/" + card + "/" + cardID + "/" + score;



export default {
    card,

    folderID,
    cardID,

    apiPrefix,
    userPrefix,

    userUserEndpoint,
    userGetFolderEndpoint,
    userPostFolderEndpoint,
    userFolderEndpoint,

    userGetCardEndpoint,
    userPostCardEndpoint,
    userCardEndpoint,
    userGetCardoverviewEndpoint,
    userScoreEndpoint,

    health,
    ping,
    version,


    publicFolderPrefix,
    getPublicFolder,
    getPublicCard,
    getPublicCardScore,
};
