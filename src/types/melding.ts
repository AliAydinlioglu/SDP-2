import type { Entity, ListResponse } from './common';
import type { User } from './user';

export interface Melding extends Entity {
    beschrijving: string;
    user: Pick<User, 'id' | 'voornaam' | 'achternaam'>;
    status: string;
    type: string;
    datum: Date;
}

export interface MeldingCreateInput {
    beschrijving: string;
    user_id: number;
    status: string;
    type: string;
    datum: Date;
}

export interface CreateMeldingRequest extends MeldingCreateInput { }
export interface UpdateMeldingRequest extends Partial<MeldingCreateInput> { }

export interface GetAllMeldingenResponse extends ListResponse<Melding> { }
export interface GetMeldingByIdResponse extends Melding { }
export interface CreateMeldingResponse extends GetMeldingByIdResponse { }
export interface UpdateMeldingResponse extends GetMeldingByIdResponse { }
