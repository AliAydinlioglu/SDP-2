import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import testData from "../testdata";
import data from "../../src/data/index";
// TEST DATA

// TESTS
let jwts: string[] = [];
let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;
let test_data: { createTestData: () => Promise<void>; deleteTestData: () => Promise<void>; getJWTs: () => string[] };

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback());
});

beforeEach(async () => {
    test_data = await testData();
    await test_data.createTestData();
    jwts = test_data.getJWTs();
});

afterEach(async () => {
    await test_data.deleteTestData();
});

afterAll(async () => {
    await server.stop();
});

it("Should delete everything in the database.", async () => {
    await data.prisma.user.deleteMany();

    const result = await data.prisma.user.findMany();

    expect(result.length).toBe(0);
});
