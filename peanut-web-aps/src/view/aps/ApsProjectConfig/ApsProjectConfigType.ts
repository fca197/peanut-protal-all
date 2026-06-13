import {request} from '@/http/axios.ts';
import {Result, ResultPageInfo} from '@@/utils/common-js.ts';

export interface ApsProjectConfig {
  saleCode: string | undefined;
  saleName: string | undefined;
  supplierStatus: string | undefined;
  isValue: number | undefined;
  parentId: string | undefined;
  id: string | undefined;

  children: ApsProjectConfig[];
}

export function queryApsProjectConfigList() {
  return request<Result<ResultPageInfo<ApsProjectConfig>>>({
    url: '/apsProjectConfig/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
