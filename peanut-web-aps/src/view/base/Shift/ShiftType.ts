import {ShiftItem} from '@v/base/ShiftItem/ShiftItemType.ts';

export interface Shift {
  shiftCode: string | undefined;
  shiftName: string | undefined;
  factoryId: string | undefined;
  id: string;
  shiftItemDtoList: ShiftItem[];
}
