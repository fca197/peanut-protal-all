import {request} from '@/http/axios';
import {getToken} from '@@/utils/cache/cookies.ts';

export interface Result<T> {
  code: number;
  data: T;
}

export interface HeaderInfo {
  fieldName: string;
  showName: string;
  width?: number | undefined;
  sortValue?: number | undefined;
}

export interface ResultPageInfo<T> {
  total: string; // 总数
  size: string; // 页面条数
  current: string; // 当前页
  records: T[]; // 数组
  dataList: T[]; // 数组
  headerList: T[]; // 数组
}

export interface ResultInfo<T> {
  data: T;
}

/** 增，删，改，提示信息 */
export function postNoResult(
  url: string,
  data: any,
  suMsg: string,
  suFun: ((data: any) => void) | undefined
) {
  request<Result<ResultInfo<any>>>({
    url,
    method: 'post',
    data,
  }).then((r) => {
    if (r.code === 200) {
      ElMessage.success(suMsg || '操作成功');
      if (suFun) {
        suFun(r.data);
      }
    }
  });
}

//  get调用获取结构
export function getResult(
  url: string,
  suMsg: string | undefined,
  suFun: ((data: any) => void) | undefined
) {
  request<Result<any>>({
    url,
    method: 'get',
  }).then((r) => {
    if (r.code === 200) {
      if (suMsg !== undefined) {
        ElMessage.success(suMsg);
      }
      if (suFun !== undefined) {
        suFun(r);
      }
    }
  });
}

// post 接口调用
export function postResultInfo(url: string, data: any) {
  return request<Result<ResultPageInfo<any>>>({
    url,
    method: 'post',
    data,
  });
}

// 查询返回列表
export function postResultInfoList(url: string, data: any): Promise<any[]> {
  return request<Result<ResultPageInfo<any>>>({
    url,
    method: 'post',
    data,
  }).then((r) => {
    // console.info("postResultInfoList ", url, data, r)
    return r.data.dataList;
  });
}

// 下载文件
export async function downloadFilePost(
  reqUrl: string,
  reqData: any,
  saveFileName: string
): Promise<void> {
  try {
    reqData = reqData || {};
    reqUrl = `/api/peanut${reqUrl}`;
    const token: string = `${getToken()}`;
    const response = await fetch(reqUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // 请求体为 JSON 格式
        Accept: 'application/octet-stream', // 期望响应为二进制流
        'j-token': token,
      },
      body: JSON.stringify(reqData), // 将请求数据转换为 JSON 字符串
    });
    ElMessage.info('下载中，请等待');
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    // 提取文件名
    let fileName = saveFileName || '未命名.xlsx';
    const contentDisposition = response.headers.get('Content-Disposition');
    if (contentDisposition) {
      const match = contentDisposition.match(
        /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
      );
      if (match != null && match[1]) {
        fileName = match[1].replace(/['"]/g, '');
      }
      if (fileName.includes('%')) {
        fileName = decodeURI(fileName);
      }
    }
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
    ElMessage.success('下载成功');
  } catch (error) {
    ElMessage.error(`文件下载失败,请稍后再试. 错误信息：${error}`);
  }
}

// 提示 敬请期待
export async function todo(data: any) {
  console.info('todo ', data);
  ElMessage.warning('敬请期待');
}

// 根据ID 获取单个详情
export async function getById(url: string, id: string) {
  return request<Result<any>>({
    url,
    method: 'post',
    data: {
      idList: [id],
    },
  }).then((t) => {
    return t.data.dataList[0];
  });
}

export interface KVEntity {
  label: any;
  value: any;
}

interface pinyin4jSzmData {
  str: string | undefined;
}

// 获取汉字首字母
export async function pinyin4jSzm(value: string | undefined) {
  if (value === undefined || value.trim().length === 0) {
    return Promise.any('');
  }
  const data: pinyin4jSzmData = {
    str: value,
  };
  return request<Result<any>>({
    url: '/pinyin4j/getSZM',
    method: 'post',
    data,
  }).then((t) => {
    return t.data.szmUpper as string;
  });
}

// 获取汉字首字母
export async function pinyin4jSzmV4(
  value: string | undefined,
  obj: any,
  fieldName: string
) {
  if (value === undefined || value.trim().length === 0) {
    return Promise.any('');
  }
  const data: pinyin4jSzmData = {
    str: value,
  };
  return request<Result<any>>({
    url: '/pinyin4j/getSZM',
    method: 'post',
    data,
  }).then((t) => {
    obj.value[fieldName] = t.data.szmUpper;
  });
}

// 获取汉字首字母
export async function pinyin4jSzmV2(
  value: string | undefined,
  oldValue: string | undefined
) {
  if (oldValue === undefined || oldValue.trim().length === 0) {
    return Promise.any('');
  }
  if (value === undefined || value.trim().length === 0) {
    return Promise.any('');
  }
  const data: pinyin4jSzmData = {
    str: value,
  };
  return request<Result<any>>({
    url: '/pinyin4j/getSZM',
    method: 'post',
    data,
  }).then((t) => {
    return t.data.szmUpper as string;
  });
}

// utils/uuid.js
export const generateSimpleUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};
