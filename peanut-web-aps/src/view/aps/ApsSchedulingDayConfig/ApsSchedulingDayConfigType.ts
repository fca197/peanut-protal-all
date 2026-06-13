import {KVEntity, postResultInfoList} from '@@/utils/common-js.ts';

export interface ApsSchedulingDayConfig {
  schedulingDayConfigId: string | undefined;
  factoryId: string | undefined;
  schedulingType: string | undefined;
  processId: string | undefined;
  makeProcessId: string | undefined;
  schedulingDayNo: string | undefined;
  schedulingDayName: string | undefined;
  isDefault: boolean | undefined;
  roomConfig: string | undefined;
  id: string | undefined;
}

export const isDefaultList: KVEntity[] = [
  {label: '是', value: true},
  {label: '否', value: false},
];
å

export function queryApsSchedulingDayConfigList(): Promise<
  ApsSchedulingDayConfig[]
> {
  return postResultInfoList('/apsSchedulingDayConfig/queryPageList', {
    queryPage: false,
  }) as Promise<ApsSchedulingDayConfig[]>;
}
