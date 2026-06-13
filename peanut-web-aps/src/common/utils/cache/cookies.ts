// 统一处理 Cookie

import {CacheKey} from '@@/constants/cache-key';
import Cookies from 'js-cookie';
import {generateSimpleUUID} from '@@/utils/common-js.ts';

export function getToken() {
  return Cookies.get(CacheKey.TOKEN);
}

export function setToken(token: string) {
  Cookies.set(CacheKey.TOKEN, token);
}

export function getValue(key: string) {
  return Cookies.get(key);
}

export function setDeviceId() {
  const xDeviceId = 'x-device-id';
  const value = generateSimpleUUID();
  setKeyValueAndTTL(xDeviceId, value, 9999999999999, true);
  return value;
}

export function getDeviceId() {
  const x = Cookies.get('x-device-id');
  if (x === undefined) {
    return setDeviceId();
  }
  return x;
}

export function setKeyValue(key: string, value: string) {
  Cookies.set(key, value);
}

export function setKeyValueAndTTL(
  key: string,
  value: string,
  lifetime: number,
  overOldValue: boolean
) {
  const ti = new Date().getTime() + lifetime;
  console.info('setKeyValueAndTTL ', key, ' v ', value, ' lifetime ', lifetime);
  if (overOldValue) {
    Cookies.set(key, value, {expires: new Date(ti), path: '/'});
  } else {
    const valueTmp = getValue(key);
    if (valueTmp === undefined) {
      Cookies.set(key, value, {expires: new Date(ti), path: '/'});
    }
  }
}

export function checkCookiesValue(
  key: string,
  ttl: number,
  fun: () => void | undefined
) {
  const value = getValue(key);
  if (value === undefined) {
    if (fun) {
      fun();
    }
    setKeyValueAndTTL(key, '1', ttl, false);
  }
}

export function removeToken() {
  console.info('删除token');
  Cookies.remove(CacheKey.TOKEN);
}
