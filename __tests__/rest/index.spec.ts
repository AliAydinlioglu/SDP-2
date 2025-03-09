import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import endpoints from "../../src/constants/endpoints";
// TEST DATA

// TESTS
let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

afterAll(async () => {
    await server.stop();
});

describe("/api", () => {
    it("should return 200", async () => {
        const response = await request.get(endpoints.apiPrefix);

        expect(response.status).toBe(200);
        expect(response.body.message).toEqual("API");
    });
});
