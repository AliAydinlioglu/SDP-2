import type { Entity, ListResponse } from "./common";
import type { Prisma } from "@prisma/client";
import type { User } from "./user";
import type { Machine } from "./machine";

export interface Onderhoud extends Entity {
    datum: Date;
    startTijd: Date;
    eindTijd: Date;
    technieker: Pick<User, "id" | "voornaam" | "achternaam">;
    reden: string;
    rapport: Prisma.JsonValue;
    opmerkingen: string;
    status: string;
    machine: Pick<Machine, "id" | "locatie">;
}

export interface OnderhoudCreateInput {
    datum: Date;
    startTijd: Date;
    eindTijd: Date;
    technieker_id: number;
    reden: string;
    rapport: Prisma.JsonValue;
    opmerkingen: string;
    status: string;
    machine_id: number;
}

export interface CreateOnderhoudRequest extends OnderhoudCreateInput {}
export interface UpdateOnderhoudRequest extends Partial<OnderhoudCreateInput> {}

export interface GetAllOnderhoudenResponse extends ListResponse<Onderhoud> {}
export interface GetOnderhoudByIdResponse extends Onderhoud {}
export interface CreateOnderhoudResponse extends GetOnderhoudByIdResponse {}
export interface UpdateOnderhoudResponse extends GetOnderhoudByIdResponse {}
