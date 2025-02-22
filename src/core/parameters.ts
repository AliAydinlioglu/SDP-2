import { Context } from "koa";

const getParams = (ctx: Context) => {
    let folderIDString = ctx.params.folderID;
    let cardIDString = ctx.params.cardID;
    let returObj: { folderID?: number; cardID?: number } = {};

    if (folderIDString !== undefined) {
        returObj.folderID = Number(folderIDString);
    }

    if (cardIDString !== undefined) {
        returObj.cardID = Number(cardIDString);
    }

    return returObj;
};

export default { getParams };
