import type { Entity, ListResponse } from './common';
import type { User } from './user';
import type { Onderhoud } from './onderhoud';
import type { Site } from './site';

export interface Machine extends Entity {
    site: Pick<Site, 'id' | 'naam'>;
    locatie: string;
    info: string;
    status: string;
    prod_status: string;
    uptime: number;
    technieker: Pick<User, 'id' | 'voornaam' | 'achternaam'>;
    onderhouden: Onderhoud[];
    dagenSindsOnderhoud: number;
    volgendOnderhoud: Date;
}

export interface MachineCreateInput {
    site_id: number;
    locatie: string;
    info: string;
    status: string;
    prod_status: string;
    uptime: number;
    technieker_id: number;
    onderhouden: Onderhoud[];
    dagenSindsOnderhoud: number;
    volgendOnderhoud: Date;
}

export interface CreateMachineRequest extends MachineCreateInput { }
export interface UpdateMachineRequest extends Partial<MachineCreateInput> { }

export interface GetAllMachinesResponse extends ListResponse<Machine> { }
export interface GetMachineByIdResponse extends Machine { }
export interface CreateMachineResponse extends GetMachineByIdResponse { }
export interface UpdateMachineResponse extends GetMachineByIdResponse { }
