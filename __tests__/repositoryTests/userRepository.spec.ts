import supertest from "supertest";
import createServer from "../../src/createServer";
import Koa from "koa";
import userService from "../../src/service/user";
import jwtUse from "../../src/core/jwtUse";
import userRepository from "../../src/repository/user";
import errorCodes from "../../src/constants/textCodes";
import testData from "../testdata";
// TEST DATA

// TESTS
let jwts: string[] = [];
let server: { getKoa: () => Koa; start: () => Promise<void>; stop: () => Promise<void> };
let request: supertest.SuperTest<supertest.Test>;
let test_data: { createTestData: () => Promise<void>; deleteTestData: () => Promise<void>; getJWTs: () => string[] };

beforeAll(async () => {
    server = await createServer();
    request = supertest(server.getKoa().callback()); // www.example.com{request} -> www.example.com/api/user/folder
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

it("Shoul delete everything in the database (deleting user will delete folders and cards)", async () => {
    await userRepository.deleteItems();

    const result = await userRepository.FindAll();

    expect(result.length).toBe(0);
});
