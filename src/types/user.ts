import type { Entity, ListResponse } from './common';
import type { Prisma, Rol } from '@prisma/client';

export interface User extends Entity {
    voornaam: string;
    achternaam: string;
    email: string;
    gsm_nr?: string;
    geboortedatum: Date;
    straat: string;
    huis_nr: string;
    stad: string;
    postcode: string;
    land: string;
    hashed_password: string;
    actief: boolean;
    rol: Rol;
}

export interface UserCreateInput {
    voornaam: string;
    achternaam: string;
    email: string;
    gsm_nr?: string;
    geboortedatum: Date;
    straat: string;
    huis_nr: string;
    stad: string;
    postcode: string;
    land: string;
    password: string;
    actief: boolean;
    roles: Rol;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    token: string;
}

export interface GetUserRequest {
    id: number | 'me';
}

export interface UserUpdateInput extends
    Omit<UserCreateInput, 'password'> { }

export interface PublicUser extends
    Pick<User, 'id' | 'voornaam' | 'achternaam' | 'email' | 'gsm_nr' | 'straat' | 'stad' | 'postcode' | 'land' | 'rol'> { }

export interface CreateUserRequest extends UserCreateInput { }
export interface UpdateUserRequest extends UserUpdateInput { }

export interface GetAllUserenResponse extends ListResponse<PublicUser> { }
export interface GetUserByIdResponse extends PublicUser { }
export interface CreateUserResponse extends GetUserByIdResponse { }
export interface UpdateUserResponse extends GetUserByIdResponse { }
