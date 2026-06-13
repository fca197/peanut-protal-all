import {KVEntity, Result, ResultPageInfo} from '@@/utils/common-js.ts';
import {request} from '@/http/axios.ts';

export interface Factory {
  factoryName: string;
  factoryCode: string;
  factoryStatus: string;
  id: string;
}

export const factoryStatusList: KVEntity[] = [
  {
    label: '启用',
    value: 'ENABLED',
  },
  // ,
  // {
  //   label: "禁用",
  //   value: "DISABLED"
  // }
];

export function queryFactoryList(): Promise<Factory[]> {
  return request<Result<ResultPageInfo<Factory>>>({
    url: '/factory/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
