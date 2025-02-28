/* eslint-disable @stylistic/indent */
const SITE_DATA = [
    {
        id: 1,
        name: 'Site 1',
        manager: 'Manager 1',
        address: 'Address 1',
    },
    {
        id: 2,
        name: 'Site 2',
        manager: 'Manager 2',
        address: 'Address 2',
    },
    {
        id: 3,
        name: 'Site 3',
        manager: 'Manager 3',
        address: 'Address 3',
    },
];

const MACHINE_DATA = [
    {
        id: 1,
        site_id: 1,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 2,
        site_id: 1,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
    {
        id: 3,
        site_id: 2,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 4,
        site_id: 2,
        status: 'stopped',
        prod_status: 'defect',
    },
    {
        id: 5,
        site_id: 3,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 6,
        site_id: 3,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
    {
        id: 7,
        site_id: 1,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 8,
        site_id: 2,
        status: 'stopped',
        prod_status: 'defect',
    },
    {
        id: 9,
        site_id: 3,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 10,
        site_id: 1,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
    {
        id: 11,
        site_id: 1,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 12,
        site_id: 2,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
    {
        id: 13,
        site_id: 3,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 14,
        site_id: 1,
        status: 'stopped',
        prod_status: 'defect',
    },
    {
        id: 15,
        site_id: 2,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 16,
        site_id: 3,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
    {
        id: 17,
        site_id: 1,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 18,
        site_id: 2,
        status: 'stopped',
        prod_status: 'defect',
    },
    {
        id: 19,
        site_id: 3,
        status: 'running',
        prod_status: 'gezond',
    },
    {
        id: 20,
        site_id: 1,
        status: 'stopped',
        prod_status: 'onderhoud',
    },
];

const MELDING_DATA = [
    {
        id: 1,
        beschrijving: 'Machine 1 is defect',

    },
    {
        id: 2,
        beschrijving: 'Je bent verplaatstt naar een andere site',

    },
    {
        id: 3,
        beschrijving: 'Machine 4 is terug opgestart',

    },
    {
        id: 4,
        beschrijving: 'Een KPI is overschreden',

    },
    {
        id: 5,
        beschrijving: 'Machine 5 is defect',

    },
];

export { SITE_DATA, MACHINE_DATA, MELDING_DATA };