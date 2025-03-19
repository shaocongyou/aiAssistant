import { type IUser } from '@/shared/model/user.model';

export interface IReadingList {
  id?: number;
  title?: string;
  status?: string;
  startDate?: Date | null;
  endDate?: Date | null;
  createdAt?: Date;
  user?: IUser | null;
}

export class ReadingList implements IReadingList {
  constructor(
    public id?: number,
    public title?: string,
    public status?: string,
    public startDate?: Date | null,
    public endDate?: Date | null,
    public createdAt?: Date,
    public user?: IUser | null,
  ) {}
}
