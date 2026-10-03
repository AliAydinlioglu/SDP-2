import winston from "winston";
import { TransformableInfo } from "logform";
const { combine, timestamp, colorize, printf } = winston.format;

// ACT AS IF THIS FILE IS A CLASS!

let rootLogger: winston.Logger;

/**
 * Get the root logger.
 */
const getLogger = () => {
    if (!rootLogger) {
        throw new Error("You must first initialize the logger");
    }

    return rootLogger;
};

/**
 * Initialize the root logger.
 *
 * @param {object} options - The options.
 * @param {string} options.level - The log level.
 * @param {boolean} options.disabled - Disable all logging.
 * @param {object} options.defaultMeta - Default metadata to show.
 */

// Entry point for logger setup.
const initializeLogger = ({ level, disabled = false, defaultMeta = {} }: { level: string; disabled?: boolean; defaultMeta?: object }) => {
    // BASICALLY A CONSTRUCTOR
    rootLogger = winston.createLogger({
        level,
        format: loggerFormat(),
        defaultMeta,
        transports: [
            new winston.transports.Console({
                silent: disabled,
            }),
        ],
    });
};

/**
 * Define the logging format. We output a timestamp, context (name), level, message and the stacktrace in case of an error
 */
const loggerFormat = () => {
    const formatMessage = ({ level, message, timestamp, name = "server", ...rest }: TransformableInfo & { name?: string }) => {
        return `${timestamp} | ${name} | ${JSON.stringify(rest.NODE_ENV)} | ${level} | ${message}`;
    };

    const formatError = ({ error, ...rest }: TransformableInfo & { error: { stack: string } | undefined }) => {
        return `${formatMessage({ ...rest, error })}\n\n${error?.stack}\n`;
    };

    const format = (info: TransformableInfo) => {
        if (info.error instanceof Error) {
            return formatError(info as TransformableInfo & { error: { stack: string } | undefined });
        } else {
            return formatMessage(info);
        }
    };
    return combine(colorize(), timestamp(), printf(format));
};

export default {
    getLogger,
    initializeLogger,
};
