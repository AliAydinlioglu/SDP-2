import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import userService from "../../src/service/user";
import jwtUse from "../../src/core/jwtUse";
import errorCodes from "../../src/constants/textCodes";
import testData from "../testdata";
import endpoints from "../../src/constants/endpoints";
import data from "../../src/data/index";
import resetDatabase from "../../src/data/resetDatabase";
import user from "../../src/service/user";
import { Rol } from "@prisma/client";
// TEST DATA

// TESTS
let jwts: string[] = [];
let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;
let test_data: { createTestData: () => Promise<void>; getJWTs: () => string[] };

let userUserEndpoint = endpoints.apiPrefix + endpoints.userPrefix;

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

beforeEach(async () => {
    await resetDatabase();
    test_data = await testData();
    await test_data.createTestData();
    jwts = test_data.getJWTs();
});

afterAll(async () => {
    await server.stop();
});

it("Test suite must at least contain 1 test", async () => {
    expect(true).toBe(true);
});
