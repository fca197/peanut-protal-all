import {type ApsRoomConfig} from '@v/aps/ApsRoomConfig/ApsRoomConfigType.ts';
import {request} from '@/http/axios.ts';
import {Result, ResultPageInfo} from '@@/utils/common-js.ts';

export interface ApsRoom {
  roomCode?: string | undefined;
  roomName?: string | undefined;
  factoryId?: string | undefined;
  id?: string | undefined;
  configList?: ApsRoomConfig[] | undefined;
}

export function queryApsRoomList() {
  return request<Result<ResultPageInfo<ApsRoom>>>({
    url: '/apsRoom/queryPageList',
    method: 'post',
    data: {
      queryPage: false,
    },
  }).then((r) => {
    return r.data.dataList;
  });
}
