import type {RouteRecordRaw} from 'vue-router';
import {createRouter} from 'vue-router';
import {routerConfig} from '@/router/config';
import {registerNavigationGuard} from '@/router/guard';
import {flatMultiLevelRoutes} from './helper';
import {menuList} from '@/router/menuList.tsx';

const Layouts = () => import('@/layouts/index.vue');

/**
 * @name 常驻路由
 * @description 除了 redirect/403/404/login 等隐藏页面，其他页面建议设置唯一的 Name 属性
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layouts,
    meta: {
      hidden: true,
    },
    children: [
      {
        path: ':path(.*)',
        component: () => import('@/pages/redirect/index.vue'),
      },
    ],
  },
  {
    path: '/403',
    component: () => import('@/pages/error/403.vue'),
    meta: {
      hidden: true,
    },
  },
  {
    path: '/404',
    component: () => import('@/pages/error/404.vue'),
    meta: {
      hidden: true,
    },
    alias: '/:pathMatch(.*)*',
  },
  {
    path: '/login',
    component: () => import('@/pages/login/index.vue'),
    meta: {
      hidden: true,
    },
  },
  {
    path: '/',
    component: Layouts,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/pages/dashboard/index.vue'),
        name: 'Dashboard',
        meta: {
          title: '首页',
          svgIcon: 'dashboard',
          affix: true,
        },
      },
      {
        path: '/aps/ApsGoodsForecastMainMake/result/:id',
        name: '制造主版本数据',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMainMake/ApsGoodsForecastMainMakeData.vue'),
        meta: {
          title: '制造主版本数据',
          elIcon: 'SuitcaseLine',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsGoodsForecastMake/result/:id',
        name: '制造版本数据',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMake/ApsGoodsForecastMakeData.vue'),
        meta: {
          title: '制造版本数据',
          elIcon: 'SuitcaseLine',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsGoodsForecastMain/result/:id',
        name: '预测主版本结果',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMain/ApsGoodsForecastMainData.vue'),
        meta: {
          title: '预测主版本结果',
          elIcon: 'DataLine',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsGoodsForecast/result/:id',
        name: '商品预测结果',
        component: () =>
          import('@/view/aps/ApsGoodsForecast/ApsGoodsForecastResult.vue'),
        meta: {
          title: '商品预测结果',
          elIcon: 'DataLine',
          hidden: true,
        },
      },
      {
        path: '/aps/MachineResult',
        name: '机器排程版本结果',
        component: () =>
          import('@/view/aps/ApsSchedulingDayConfigVersion/MachineResult.vue'),
        meta: {
          title: '机器排程版本结果',
          elIcon: 'Files',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsSchedulingDayConfigVersion/MachineResult/:id/:factoryId',
        name: '机器排程版本结果',
        component: () =>
          import('@/view/aps/ApsSchedulingDayConfigVersion/MachineResult.vue'),
        meta: {
          title: '机器排程版本结果',
          elIcon: 'Files',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsSchedulingDayConfigVersion/ProcessLineResult/:id',
        name: '工艺路径排程版本结果',
        component: () =>
          import('@/view/aps/ApsSchedulingDayConfigVersion/ProcessLineResult.vue'),
        meta: {
          title: '工艺路径排程版本结果',
          elIcon: 'Files',
          hidden: true,
        },
      },
      {
        path: '/aps/CreateScheduling/:id/:operType/:step',
        name: '排产创建',
        component: () =>
          import('@/view/aps/ApsSchedulingVersion/CreateScheduling.vue'),
        meta: {
          title: '排产创建',
          elIcon: 'Refrigerator',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsOrderGoodsBomKittingVersionOrder/:id',
        name: '齐套订单概览',
        component: () =>
          import('@/view/aps/ApsOrderGoodsBomKittingVersionOrder/ApsOrderGoodsBomKittingVersionOrderIndex.vue'),
        meta: {
          title: '齐套订单概览',
          elIcon: 'Refrigerator',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsOrderGoodsBomKittingVersionOrderBom/:versionId/:orderId',
        name: '齐套BOM详情',
        component: () =>
          import('@v/aps/ApsOrderGoodsBomKittingVersionOrderBom/ApsOrderGoodsBomKittingVersionOrderBomIndex.vue'),
        meta: {
          title: '齐套BOM详情',
          elIcon: 'Refrigerator',
          hidden: true,
        },
      },
    ],
  },
];

/**
 * @name 动态路由
 * @description 用来放置有权限 (Roles 属性) 的路由
 * @description 必须带有唯一的 Name 属性
 */
export const dynamicRoutes: RouteRecordRaw[] = menuList();

/** 路由实例 */
export const router = createRouter({
  history: routerConfig.history,
  routes: routerConfig.thirdLevelRouteCache
    ? flatMultiLevelRoutes(constantRoutes)
    : constantRoutes,
});

/** 重置路由 */
export function resetRouter() {
  try {
    // 注意：所有动态路由路由必须带有 Name 属性，否则可能会不能完全重置干净
    router.getRoutes().forEach((route) => {
      const {name, meta} = route;
      if (name && meta.roles?.length) {
        router.hasRoute(name) && router.removeRoute(name);
      }
    });
  } catch {
    // 强制刷新浏览器也行，只是交互体验不是很好
    location.reload();
  }
}

// 注册路由导航守卫
registerNavigationGuard(router);
