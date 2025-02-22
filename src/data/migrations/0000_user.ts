import { Knex } from "knex";
import data from "../index";

const up = async (knex: Knex) => {
    await knex.schema.createTable(data.tables.user, (table) => {
        table.increments("id"); // creates an auto incrementing column called id, data type is integer.

        table.string("name", 255).notNullable(); // creates a column called name, data type is string, max length is 255, cannot be null.

        table.string("email", 255).notNullable().unique({ indexName: "idx_user_email_unique" }); // creates a column called email, data type is string, max length is 255, cannot be null.

        table.string("hashed_password", 255).notNullable(); // creates a column called password, data type is string, max length is 255, cannot be null.
    });
};

const down = (knex: Knex) => {
    return knex.schema.dropTableIfExists(data.tables.user);
};

export { up, down }; // We cannot use export default here because of the way knex handles migrations. It directly looks for the up and down functions and not a custom "name".
