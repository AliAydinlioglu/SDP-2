import Joi from "joi";
import { Context, Next } from "koa";
import jwtUse from "./jwtUse";
import textCodes from "../constants/textCodes";
import parameters from "./parameters";
import { ServiceError } from "./errorHandler";
import data from "../data";

interface Schema {
    headers?: Joi.StringSchema<string>;
    body?: Joi.ObjectSchema<any>;
    params?: Joi.ObjectSchema<any>;
}

const validateSchema = (...schemas: Schema[]) => {
    return async (ctx: Context, next: Next) => {
        let authorizationInput = ctx.request.headers.authorization;
        let bodyInput = ctx.request.body;
        let paramsInput = parameters.getParams(ctx);

        for (let schema of schemas) {
            if (schema.headers !== undefined) {
                let { error } = schema.headers.validate(authorizationInput);

                if (error && error.details[0].type === "any.required") {
                    // any.required is the error type for undefined.
                    throw new ServiceError(textCodes.NOJWT, 401);
                } else if (error) {
                    ctx.throw(500, error);
                } else {
                    let token = authorizationInput!.split(" ")[1]; // Bearer <token>

                    let user_id = jwtUse.getUserID(token);

                    if (user_id == false) {
                        throw new ServiceError(textCodes.INVALIDJWT, 401);
                    } else if (!(await data.prisma.user.findUnique({ where: { id: user_id } }))) {
                        throw new ServiceError(textCodes.USERMISSING, 401);
                    } else {
                        ctx.user_id = user_id;
                    }
                }
            }

            if (schema.body !== undefined) {
                let { error } = schema.body.validate(bodyInput);

                if (error) {
                    throw new ServiceError((error as Error).message, 400);
                }
            }

            if (schema.params !== undefined) {
                let { error } = schema.params.validate(paramsInput);

                if (error) {
                    throw new ServiceError((error as Error).message, 400);
                }
            }
        }
        return next(); // Normally we would have to await next(), but returning it has the same effect. I don't know why, but don't care enough to find out.
    };
};

// We simply check if the JWT "looks" ok or not, we're not validating the signature of the jwt YET.
const headerAuthorizationSchema = {
    headers: Joi.string()
        .required()
        .pattern(/^Bearer\s[\w-]+\.[\w-]+\.[\w-]+$/),
};

export default { validateSchema, headerAuthorizationSchema };
