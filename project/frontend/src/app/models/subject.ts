import { User } from './user';

export interface Subject {
  id?: number;
  name: string;
  enrolledUsers?: User[];
}

