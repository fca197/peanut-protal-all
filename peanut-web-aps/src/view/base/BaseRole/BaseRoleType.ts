import {postResultInfo} from '@@/utils/common-js.ts';

export interface BaseRole {
  roleCode: string;
  roleName: string;
  roleGroupId: string;
  id: string;
}

export function getRoleList(): Promise<BaseRole[]> {
  return postResultInfo(`/baseRole/queryPageList`, {
    queryPage: false,
  }).then((t) => {
    return t.data.dataList;
  });
}
