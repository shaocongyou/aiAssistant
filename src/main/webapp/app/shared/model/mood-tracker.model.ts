import { type IUser } from '@/shared/model/user.model';

export interface IMoodTracker {
  id?: number;
  mood?: string;
  intensity?: number;
  date?: Date;
  createdAt?: Date;
  user?: IUser | null;
}

export class MoodTracker implements IMoodTracker {
  constructor(
    public id?: number,
    public mood?: string,
    public intensity?: number,
    public date?: Date,
    public createdAt?: Date,
    public user?: IUser | null,
  ) {}
}
