import createServer from "./createServer";

const main = async () => {
    try {
        const server = await createServer();
        await server.start();

        const onClose = async () => {
            await server.stop();
            process.exit(0);
        };
        

        process.on("SIGTERM", onClose);
        process.on("SIGQUIT", onClose);
    } catch (error) {
        console.error(error);
        process.exit(-1);
    }
};

main();
