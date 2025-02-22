import knex from "knex";
import data from ".";

const EMPTYDATABASE = async () => {
    await data.getKnex().schema.dropTableIfExists(data.tables.scoreboard);
    await data.getKnex().schema.dropTableIfExists(data.tables.card);
    await data.getKnex().schema.dropTableIfExists(data.tables.folder);
    await data.getKnex().schema.dropTableIfExists(data.tables.user);
    await data.getKnex().schema.dropTableIfExists("knex_meta");
    await data.getKnex().schema.dropTableIfExists("knex_meta_lock");
};

export default EMPTYDATABASE;
