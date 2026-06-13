import {KVEntity} from '@/common/utils/common-js';

export interface ApsSchedulingDayConfigVersion {
  schedulingDayConfigId: string | undefined;
  factoryId: string | undefined;
  schedulingDayVersionNo: string | undefined;
  schedulingDay: string | undefined;
  searchOld: boolean | undefined;
  isIssuedThird: string | undefined;
  processId: string | undefined;
  headerList: string | undefined;
  productType: string | undefined;
  goodsIdList: string | undefined;
  saleConfigIdList: string | undefined;
  stepIndex: number | undefined;
  orderFieldList: string | undefined;
  orderUserFieldList: string | undefined;
  id: string | undefined;
}

export const boolArr: KVEntity[] = [
  {label: '是', value: true},
  {label: '否', value: false},
];
