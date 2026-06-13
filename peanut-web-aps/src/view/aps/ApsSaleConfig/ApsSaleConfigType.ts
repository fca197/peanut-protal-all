import {Result, ResultPageInfo} from '@/common/utils/common-js';
import {request} from '@/http/axios.ts';

export interface ApsSaleConfig {
  saleCode: string | undefined;
  saleName: string | undefined;
  supplierStatus: string | undefined;
  isValue: number | undefined;
  parentId: string | undefined;
  id: string | undefined;
  children: ApsSaleConfig[];
}

export function querySaleConfigList() {
  return request<Result<ResultPageInfo<ApsSaleConfig>>>({
    url: '/apsSaleConfig/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}

export function querySaleGroupConfigList() {
  return request<Result<ResultPageInfo<ApsSaleConfig>>>({
    url: '/apsSaleConfig/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
      data: {
        isValue: 0,
      },
    },
  }).then((r) => {
    return r.data.dataList;
  });
}

export function querySaleConfigByIdList(idList: string[]) {
  return request<Result<ResultPageInfo<ApsSaleConfig>>>({
    url: '/apsSaleConfig/queryByIdList',
    method: 'post',
    data: {
      idList,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
