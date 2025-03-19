import { type IUser } from '@/shared/model/user.model';

export interface ISocialConnection {
  id?: number;
  friendUsername?: string;
  status?: string;
  createdAt?: Date;
  users?: IUser[] | null;
}

export class SocialConnection implements ISocialConnection {
  constructor(
    public id?: number,
    public friendUsername?: string,
    public status?: string,
    public createdAt?: Date,
    public users?: IUser[] | null,
  ) {}
}
