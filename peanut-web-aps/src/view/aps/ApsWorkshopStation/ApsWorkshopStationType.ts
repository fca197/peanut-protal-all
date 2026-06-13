import {request} from '@/http/axios.ts';
import {Result, ResultPageInfo} from '@@/utils/common-js.ts';

export interface ApsWorkshopStation {
  factoryId: string;
  sectionId: string;
  stationName: string;
  stationCode: string;
  stationType: string;
  stationStatus: string;
  id: string;
}

export function queryStationList() {
  return request<Result<ResultPageInfo<ApsWorkshopStation>>>({
    url: '/workshopStation/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
