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
];

export { SITE_DATA, MACHINE_DATA };