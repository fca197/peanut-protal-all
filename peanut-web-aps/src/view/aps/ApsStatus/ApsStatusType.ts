import {request} from '@/http/axios.ts';
import {type Result, type ResultPageInfo} from '@@/utils/common-js.ts';

export interface ApsStatus {
  statusCode: string;
  statusName: string;
  sortIndex: number;
  orderStatusId: string;
  id: string | number;
}

export function queryApsStatusList() {
  return request<Result<ResultPageInfo<ApsStatus>>>({
    url: '/apsStatus/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
