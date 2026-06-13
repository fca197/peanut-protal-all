/* eslint-disable perfectionist/sort-imports */

// core
import {pinia} from '@/pinia';
import {router} from '@/router';
import {installPlugins} from '@/plugins';
import App from '@/App.vue';
// css
import 'normalize.css';
import 'nprogress/nprogress.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import '@@/assets/styles/index.scss';
import '@/common/assets/styles/style.css';
import 'element-plus/dist/index.css';
import {initBaiduAnalytics} from '@/common/utils/baiduAnalytics';

// 初始化全局变量
window.global = window;
window.process = {env: {}};
// 创建应用实例
const app = createApp(App);
// 安装插件（全局组件、自定义指令等）
installPlugins(app);

// 初始化百度统计（替换为你的 ID）
initBaiduAnalytics('74466160c005f4fe5c7c40b4e1c06610');

// 安装 pinia 和 router
app.use(pinia).use(router);

// router 准备就绪后挂载应用
router.isReady().then(() => {
  app.mount('#app');
});
