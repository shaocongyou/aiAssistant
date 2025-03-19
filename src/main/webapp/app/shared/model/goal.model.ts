import { type IUser } from '@/shared/model/user.model';

import { type GoalType } from '@/shared/model/enumerations/goal-type.model';
export interface IGoal {
  id?: number;
  title?: string;
  description?: string | null;
  goalType?: keyof typeof GoalType;
  deadline?: Date | null;
  completed?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
  user?: IUser | null;
}

export class Goal implements IGoal {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public goalType?: keyof typeof GoalType,
    public deadline?: Date | null,
    public completed?: boolean,
    public createdAt?: Date,
    public updatedAt?: Date,
    public user?: IUser | null,
  ) {
    this.completed = this.completed ?? false;
  }
}
