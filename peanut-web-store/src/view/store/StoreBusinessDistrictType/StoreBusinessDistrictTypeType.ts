import {postResultInfoListV2} from "@@/utils/common-js.ts";

export interface StoreBusinessDistrictType {
  id?: string | undefined
  businessDistrictTypeName?: string | undefined
  businessDistrictTypeCode?: string | undefined
  businessDistrictTypeDesc?: string | undefined
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

export function queryStoreBusinessDistrictTypeList(targerData: any): Promise<any []> {
  return postResultInfoListV2("/storeBusinessDistrictType/queryList", {}, targerData)
}