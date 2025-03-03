import Rol from "./rol";

type User = {
    id?: number;
    voornaam: string;
    achternaam: string;
    email: string;
    password: string;
    straat: string;
    huis_nr: string;
    postcode: string;
    stad: string;
    land: string;
    gsm_nr?: string;
    actief?: boolean;
    rol: Rol;
};

type PasswordlessUser = {
    id: number;
    voornaam: string;
    achternaam: string;
    email: string;
    straat: string;
    huis_nr: string;
    postcode: string;
    stad: string;
    land: string;
    gsm_nr?: string;
    actief: boolean;
    rol: Rol;
};

type DBUser = {
    id: number;
    voornaam: string;
    achternaam: string;
    email: string;
    hashed_password: string;
    straat: string;
    huis_nr: string;
    postcode: string;
    stad: string;
    land: string;
    gsm_nr?: string | null;
    actief: boolean;
    rol: Rol;
};

export { User, PasswordlessUser, DBUser };
