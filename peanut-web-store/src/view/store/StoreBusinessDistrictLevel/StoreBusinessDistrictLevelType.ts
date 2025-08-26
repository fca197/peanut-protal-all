import {postResultInfoList} from "@@/utils/common-js.ts";

export interface StoreBusinessDistrictLevel {
  id?: string | undefined
  businessDistrictLevelName?: string | undefined
  businessDistrictLevelDesc?: string | undefined
  businessDistrictLevelColor?: string | undefined
  isDelete?: string | undefined
  createTime?: string | undefined
  createBy?: string | undefined
  updateTime?: string | undefined
  updateBy?: string | undefined
  traceId?: string | undefined
  versionNum?: string | undefined
  tenantId?: string | undefined
  createUserName?: string | undefined
  updateUserName?: string | undefined
}

export function queryLevelList(){
  return postResultInfoList("/storeBusinessDistrictLevel/queryList",{})
}