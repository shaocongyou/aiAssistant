import { type IUser } from '@/shared/model/user.model';

import { type HabitType } from '@/shared/model/enumerations/habit-type.model';
export interface IHabit {
  id?: number;
  name?: string;
  habitType?: keyof typeof HabitType;
  frequency?: string;
  startDate?: Date | null;
  active?: boolean;
  user?: IUser | null;
}

export class Habit implements IHabit {
  constructor(
    public id?: number,
    public name?: string,
    public habitType?: keyof typeof HabitType,
    public frequency?: string,
    public startDate?: Date | null,
    public active?: boolean,
    public user?: IUser | null,
  ) {
    this.active = this.active ?? false;
  }
}
