import { type IUser } from '@/shared/model/user.model';
import { type ITask } from '@/shared/model/task.model';

export interface IReminder {
  id?: number;
  message?: string;
  reminderTime?: Date;
  repeatInterval?: string | null;
  createdAt?: Date;
  user?: IUser | null;
  task?: ITask | null;
}

export class Reminder implements IReminder {
  constructor(
    public id?: number,
    public message?: string,
    public reminderTime?: Date,
    public repeatInterval?: string | null,
    public createdAt?: Date,
    public user?: IUser | null,
    public task?: ITask | null,
  ) {}
}
