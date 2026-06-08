import { Subject } from './subject';

export interface Grade {
  id?: number;
  value: number;
  description: string;
  subject: Subject;
  student: {
    id: number;
    username: string;
  };
}
