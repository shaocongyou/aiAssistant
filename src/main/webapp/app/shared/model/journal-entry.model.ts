import { type IUser } from '@/shared/model/user.model';

export interface IJournalEntry {
  id?: number;
  title?: string;
  contentContentType?: string;
  content?: string;
  mood?: string | null;
  createdAt?: Date;
  user?: IUser | null;
}

export class JournalEntry implements IJournalEntry {
  constructor(
    public id?: number,
    public title?: string,
    public contentContentType?: string,
    public content?: string,
    public mood?: string | null,
    public createdAt?: Date,
    public user?: IUser | null,
  ) {}
}
