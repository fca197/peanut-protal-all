import {request} from '@/http/axios.ts';
import {Result, ResultPageInfo} from '@@/utils/common-js.ts';

export interface ApsWorkshopSection {
  factoryId: string;
  sectionName: string;
  sectionCode: string;
  sectionType: string;
  sectionStatus: string;
  id: string;
  rowIndex: number;
}

export function querySectionList() {
  return request<Result<ResultPageInfo<ApsWorkshopSection>>>({
    url: '/workshopSection/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
