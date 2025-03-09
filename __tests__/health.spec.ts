import supertest from "supertest";
import createServer from "../src/createServer";
import Koa from "koa";
import endpoints from "../src/constants/endpoints";
import testEndpoints from "./testEndpoints";
// TEST DATA

// TESTS
let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;
let test_data: { createTestData: () => Promise<void>; deleteTestData: () => Promise<void>; getJWTs: () => string[] };

let userUserEndpoint = endpoints.apiPrefix + endpoints.userPrefix;

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

afterAll(async () => {
    await server.stop();
});

describe("/health", () => {
    describe("/ping", () => {
        it("should return 200", async () => {
            const response = await request.get(testEndpoints.apiHealthPing);

            expect(response.status).toBe(200);
        });
    });

    describe("/version", () => {
        it("should return 200", async () => {
            const response = await request.get(testEndpoints.apiHealthVersion);

            expect(response.status).toBe(200);
        });
    });
});
