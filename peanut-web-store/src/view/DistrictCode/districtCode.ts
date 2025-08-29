import {postResultInfoList} from "@@/utils/common-js.ts";

export function queryDistrictCode(parentCode: string | undefined, level: number[] = []): Promise<DistrictCode[]> {

  return postResultInfoList("/districtCode/queryList", {
    data: {parentCode: parentCode, levelList: level}
  });
}

export function queryDistrictCodeV2(parentCode: string | undefined, target: any) {
  postResultInfoList("/districtCode/queryList", {
    data: {parentCode: parentCode}
  }).then((r) => target.value = r);
}

export interface DistrictCode {
  id?: string | undefined
  code?: string | undefined
  cityCode?: string | undefined
  name?: string | undefined
  parentCode?: string | undefined
  path?: string | undefined
  level?: string | undefined
  centerLng?: string | undefined
  centerLat?: string | undefined
  pathName?: string | undefined
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
