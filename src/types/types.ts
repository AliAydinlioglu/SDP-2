type User = {
    id?: number;
    voornaam: string;     
    achternaam: string;
    email: string;
    password: string;
    adres: string;
    gsm?: string;
    actief?: boolean;
     };
type PasswordlessUser = {
    id: number;
    voornaam: string;     
    achternaam: string;
    email: string;
    adres: string;
    gsm?: string;
    actief: boolean;
};

type DBUser = {
    id: number;
    voornaam: string;
    achternaam: string;
    email: string;
    hashed_password: string;
    adres: string;
    gsm?: string | null;
    actief: boolean;
     };

export { User, PasswordlessUser, DBUser };
