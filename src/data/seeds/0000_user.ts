import userService from "../../service/user";
import userRepository from "../../repository/user";

let jwts: string[] = [];

let emailBase = "@example.com";
let emails = ["user" + emailBase, "rosalindmyers" + emailBase, "somi" + emailBase];

const seed = async () => {
    // first delete all entries
    await userRepository.deleteItems("email", emails[0]);
    await userRepository.deleteItems("email", emails[1]);
    await userRepository.deleteItems("email", emails[2]);

    // add the entries
    const userData = [
        { name: "John Doe", email: emails[0], password: "user1234" },
        { name: "Rosalind Myers", email: emails[1], password: "12345678910" },
        { name: "So Mi", email: emails[2], password: "12345678911" },
    ];

    for (let user of userData) {
        let jwt = await userService.create(user);

        jwts.push(jwt);
    }
};

export { seed, jwts }; // We cannot use export default here because of the way knex handles migrations.
