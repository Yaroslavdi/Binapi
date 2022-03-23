import { Moment } from 'moment';

export interface ITODO {
  id?: number;
  name?: string;
  date?: string;
}

export const defaultValue: Readonly<ITODO> = {};
