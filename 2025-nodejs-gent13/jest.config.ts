/**
 * For a detailed explanation regarding each configuration property, visit:
 * https://jestjs.io/docs/configuration
 */

import type { Config } from "jest";

const config: Config = {
    // An array of glob patterns indicating a set of files for which coverage information should be collected
    collectCoverageFrom: ["./src/service/**/*.ts", "./src/rest/**/*.ts"],
    // The directory where Jest should output its coverage files
    coverageDirectory: "__tests__/coverage",
    // Indicates which provider should be used to instrument code for coverage
    coverageProvider: "v8",
    // A preset that is used as a base for Jest's configuration
    preset: "ts-jest",
    // The test environment that will be used for testing
    testEnvironment: "jest-environment-node",
    // The glob patterns Jest uses to detect test files
    testMatch: ["**/__tests__/**/*spec.[t]s?(x)"],
    testPathIgnorePatterns: ['/node_modules/', '/out/'],
};

export default config;
