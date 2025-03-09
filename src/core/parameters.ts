import { Context } from "koa";

// We need to rewrite this and make endpoints that require this function to be able to test it.

const getParams = (ctx: Context) => {
    // get the parameters from the url
    let param1String = ctx.params.param1url;
    let param2String = ctx.params.param2url;

    // create an object to store the parameters
    let returObj: { param1?: number; param2?: number } = {};

    // check if the parameters are defined
    if (param1String !== undefined) {
        returObj.param1 = Number(param1String);
    }

    if (param2String !== undefined) {
        returObj.param2 = Number(param2String);
    }

    return returObj;
};

export default { getParams };
