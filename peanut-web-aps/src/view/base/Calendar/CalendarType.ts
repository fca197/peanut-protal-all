import {KVEntity} from '@@/utils/common-js.ts';

export interface Calendar {
  factoryId: string | undefined;
  calendarName: string | undefined;
  calendarCode: string | undefined;
  calendarType: string | undefined;
  calendarDesc: string | undefined;
  calendarDisabled: boolean | undefined;
  id: string | undefined;
}

export const calendarDisabledList: KVEntity[] = [
  {label: '启用', value: false},
  {label: '禁用', value: true},
];
