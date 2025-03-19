import { type IUser } from '@/shared/model/user.model';
import { type IGoal } from '@/shared/model/goal.model';

import { type TaskStatus } from '@/shared/model/enumerations/task-status.model';
export interface ITask {
  id?: number;
  title?: string;
  description?: string | null;
  status?: keyof typeof TaskStatus;
  dueDate?: Date | null;
  priority?: number;
  createdAt?: Date;
  updatedAt?: Date;
  user?: IUser | null;
  goal?: IGoal | null;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public status?: keyof typeof TaskStatus,
    public dueDate?: Date | null,
    public priority?: number,
    public createdAt?: Date,
    public updatedAt?: Date,
    public user?: IUser | null,
    public goal?: IGoal | null,
  ) {}
}
