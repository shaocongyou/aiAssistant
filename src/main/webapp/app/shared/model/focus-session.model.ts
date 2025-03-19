import { type IUser } from '@/shared/model/user.model';

export interface IFocusSession {
  id?: number;
  duration?: number;
  task?: string | null;
  date?: Date;
  createdAt?: Date;
  user?: IUser | null;
}

export class FocusSession implements IFocusSession {
  constructor(
    public id?: number,
    public duration?: number,
    public task?: string | null,
    public date?: Date,
    public createdAt?: Date,
    public user?: IUser | null,
  ) {}
}
