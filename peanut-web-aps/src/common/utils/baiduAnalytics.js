// utils/baiduAnalytics.js
export const initBaiduAnalytics = (id) => {
  if (typeof window === 'undefined') {
    return;
  } // 避免 SSR 错误
    // 初始化百度统计
  window._hmt = window._hmt || [];
  const hm = document.createElement('script');
  hm.src = `https://hm.baidu.com/hm.js?${id}`;
  const s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(hm, s);
};

// 页面访问统计
export const trackPageView = (url) => {
  if (typeof window === 'undefined' || !window._hmt) {
    return;
  }
  window._hmt.push(['_trackPageview', url]);
};

// 事件统计
// 记录登录事件
// trackEvent('User', 'Login', 'Success', 1);
export const trackEvent = (category, action, label = '', value = 0) => {
  if (typeof window === 'undefined' || !window._hmt) {
    return;
  }
  window._hmt.push(['_trackEvent', category, action, label, value]);
};
