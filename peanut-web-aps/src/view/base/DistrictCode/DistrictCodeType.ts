import {KVEntity, Result, ResultPageInfo} from '@@/utils/common-js.ts';
import {request} from '@/http/axios.ts';

export interface DistrictCode {
  code: string;
  name: string;
  parentCode: string;
  path: string;
  level: number;
  id: string;
}

export const districtCodeLevel: KVEntity[] = [
  {
    label: '省份',
    value: 1,
  },
  {
    label: '城市',
    value: 2,
  },
  {
    label: '区县',
    value: 3,
  },
];

export function queryDistrictByParentCode(data: string) {
  return request<Result<ResultPageInfo<DistrictCode>>>({
    url: '/districtCode/queryList',
    method: 'POST',
    data: {data: {parentCode: data || '0'}},
  }).then((r) => {
    return r.data.dataList;
  });
}
