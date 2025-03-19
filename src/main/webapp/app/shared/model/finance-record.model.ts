import { type IUser } from '@/shared/model/user.model';

export interface IFinanceRecord {
  id?: number;
  description?: string;
  amount?: number;
  category?: string;
  date?: Date;
  createdAt?: Date;
  user?: IUser | null;
}

export class FinanceRecord implements IFinanceRecord {
  constructor(
    public id?: number,
    public description?: string,
    public amount?: number,
    public category?: string,
    public date?: Date,
    public createdAt?: Date,
    public user?: IUser | null,
  ) {}
}
