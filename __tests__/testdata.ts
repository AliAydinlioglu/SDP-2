import jwtUse from "../src/core/jwtUse";
import userService from "../src/service/user";
import userRepository from "../src/repository/user";

// TEST DATA

let jwts: string[] = [];

const testData = async () => {
    const getJWTs = () => {
        return jwts;
    };

    const createTestData = async () => {
        jwts = [];
        let userData = [
            { name: "Solomon Reed", email: `solomonreed@email.com`, password: "therealslimshady" },
            { name: "Rosalind Myers", email: "rosalindmyers@email.com", password: "paperplanes" },
            { name: "So Mi", email: "somi@email.com", password: "thepretender" },
        ];

        for (let user of userData) {
            let jwt = await userService.create(user);

            jwts.push(jwt);
        }
    };

    const deleteTestData = async () => {
        // deleting users will delete everything else due to foreign key constraints.
        for (let jwt of jwts) {
            let id = jwtUse.getUserID(jwt);
            await userRepository.deleteItems("id", id);
        }
    };

    return { createTestData, deleteTestData, getJWTs };
};

export default testData;
