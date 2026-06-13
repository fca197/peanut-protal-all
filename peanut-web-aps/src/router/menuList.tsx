import type {RouteRecordRaw} from 'vue-router';

const Layouts = () => import('@/layouts/index.vue');

const allMenuList: RouteRecordRaw[] = [
  {
    path: '/base/system',
    name: '系统设置',
    redirect: '',
    meta: {
      title: '系统设置',
      elIcon: 'Setting',
    },
    component: Layouts,
    children: [
      {
        path: '/base/WebSocket',
        name: 'webSocket',
        component: () => import('@/view/websocket/WebSocketIndex.vue'),
        meta: {
          title: 'webSocket',
          elIcon: 'Switch',
          hidden: true,
        },
      },
      {
        path: '/base/BaseResource',
        name: '菜单',
        component: () =>
          import('@/view/base/BaseResource/BaseResourceIndex.vue'),
        meta: {
          title: '菜单',
          elIcon: 'Menu',
          hidden: true,
        },
      },
      {
        path: '/base/BaseOplog',
        name: '日志',
        component: () => import('@/view/base/BaseOplog/BaseOplogIndex.vue'),
        meta: {
          title: '日志',
          elIcon: 'Memo',
        },
      },
      {
        path: '/base/DistrictCode',
        name: '区域编码',
        component: () => import('@v/base/DistrictCode/DistrictCodeIndex.vue'),
        meta: {
          title: '区域编码',
          elIcon: 'Location',
        },
      },
      {
        path: '/base/app',
        name: 'APP',
        component: () => import('@/view/base/BaseApp/BaseAppIndex.vue'),
        meta: {
          title: 'App',
          elIcon: 'Compass',
          hidden: true,
        },
      },
      {
        path: '/base/BaseAppResource',
        name: 'APP菜单',
        component: () =>
          import('@/view/base/BaseAppResource/BaseAppResourceIndex.vue'),
        meta: {
          title: 'App菜单',
          elIcon: 'Memo',
          hidden: true,
        },
      },
      {
        path: '/base/Dictionary',
        name: '字典',
        component: () => import('@/view/base/Dictionary/DictionaryIndex.vue'),
        meta: {
          title: '字典',
          elIcon: 'Reading',
        },
      },
      {
        path: '/base/BaseTableHeader',
        name: '动态表头',
        component: () =>
          import('@/view/base/BaseTableHeader/BaseTableHeaderIndex.vue'),
        meta: {
          title: '动态表头',
          elIcon: 'Coin',
          hidden: true,
        },
      },
      {
        path: '/base/LoginAccount',
        name: '登录账户',
        component: () => import('@/view/base/LoginAccount/index.vue'),
        meta: {
          title: '登录账户',
          elIcon: 'User',
          hidden: true,
        },
      },
      {
        path: '/base/BaseRoleGroup',
        name: '角色组信息',
        component: () =>
          import('@/view/base/BaseRoleGroup/BaseRoleGroupIndex.vue'),
        meta: {
          title: '角色组信息',
          elIcon: 'UserFilled',
          hidden: true,
        },
      },
      {
        path: '/base/BaseRole',
        name: '角色信息',
        component: () => import('@/view/base/BaseRole/BaseRoleIndex.vue'),
        meta: {
          title: '角色信息',
          elIcon: 'Avatar',
          hidden: true,
        },
      },
    ],
  },
  {
    path: '/base/factory',
    name: '工厂信息',
    redirect: '',
    meta: {
      title: '工厂信息',
      elIcon: 'OfficeBuilding',
    },
    component: Layouts,
    children: [
      {
        path: '/base/Factory',
        name: '工厂',
        component: () => import('@/view/base/Factory/FactoryIndex.vue'),
        meta: {
          title: '工厂',
          elIcon: 'OfficeBuilding',
        },
      },
      {
        path: '/base/Calendar',
        name: '日历',
        component: () => import('@v/base/Calendar/CalendarIndex.vue'),
        meta: {
          title: '日历',
          elIcon: 'Calendar',
        },
      },
      {
        path: '/base/Shift',
        name: '班次',
        component: () => import('@/view/base/Shift/ShiftIndex.vue'),
        meta: {
          title: '班次',
          elIcon: 'Coin',
        },
      },
      {
        path: '/aps/ApsStatus',
        name: '状态',
        component: () => import('@/view/aps/ApsStatus/ApsStatusIndex.vue'),
        meta: {
          title: '状态',
          elIcon: 'Soccer',
        },
      },
      {
        path: '/aps/ApsWorkshopSection',
        name: '工段',
        component: () =>
          import('@/view/aps/ApsWorkshopSection/ApsWorkshopSectionIndex.vue'),
        meta: {
          title: '工段',
          elIcon: 'SemiSelect',
        },
      },
      {
        path: '/aps/ApsWorkshopStation',
        name: '工位',
        component: () =>
          import('@/view/aps/ApsWorkshopStation/ApsWorkshopStationIndex.vue'),
        meta: {
          title: '工位',
          elIcon: 'Aim',
        },
      },
      {
        path: '/aps/ApsRoom',
        name: '工作车间',
        component: () => import('@/view/aps/ApsRoom/ApsRoomIndex.vue'),
        meta: {
          title: '工作车间',
          elIcon: 'House',
        },
      },
      {
        path: '/aps/ApsProcessPath',
        name: '工艺路径',
        component: () =>
          import('@/view/aps/ApsProcessPath/ApsProcessPathIndex.vue'),
        meta: {
          title: '工艺路径',
          elIcon: 'Paperclip',
        },
      },
      {
        path: '/aps/ApsMachine',
        name: '机器',
        component: () => import('@v/aps/ApsMachine/ApsMachineIndex.vue'),
        meta: {
          title: '机器',
          elIcon: 'Coin',
        },
      },
      {
        path: '/aps/ApsMachineWorkstation',
        name: '机器工作站',
        component: () =>
          import('@v/aps/ApsMachineWorkstation/ApsMachineWorkstationIndex.vue'),
        meta: {
          title: '机器工作站',
          elIcon: 'MessageBox',
        },
      },
      {
        path: '/aps/ApsProduceProcess',
        name: '制造路径',
        component: () =>
          import('@v/aps/ApsProduceProcess/ApsProduceProcessIndex.vue'),
        meta: {
          title: '制造路径',
          elIcon: 'Connection',
        },
      },
      {
        path: '/base/BaseSupplier',
        name: '供应商',
        component: () =>
          import('@/view/base/BaseSupplier/BaseSupplierIndex.vue'),
        meta: {
          title: '供应商',
          elIcon: 'UserFilled',
        },
      },
      {
        path: '/aps/ApsLogisticsPath',
        name: '物流路径',
        component: () =>
          import('@/view/aps/ApsLogisticsPath/ApsLogisticsPathIndex.vue'),
        meta: {
          title: '物流路径',
          elIcon: 'Operation',
        },
      },
      {
        path: '/base/BaseReportConfigUser',
        name: '用户报表',
        component: () =>
          import('@/view/base/BaseReportConfigUser/BaseReportConfigUserIndex.vue'),
        meta: {
          title: '用户报表',
          elIcon: 'Notebook',
          hidden: true,
        },
      },
      {
        path: '/base/BaseReportConfig',
        name: '报表',
        component: () =>
          import('@/view/base/BaseReportConfig/BaseReportConfigIndex.vue'),
        meta: {
          title: '报表',
          elIcon: 'Grid',
        },
      },
      {
        path: '/aps/ApsBomSupplier',
        name: '零件供应商',
        component: () =>
          import('@v/aps/ApsBomSupplier/ApsBomSupplierIndex.vue'),
        meta: {
          title: '零件供应商',
          elIcon: 'Avatar',
        },
      },
    ],
  },
  {
    path: '/base/goods',
    name: '商品信息',
    redirect: '',
    meta: {
      title: '商品信息',
      elIcon: 'Goods',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsBom',
        name: '零件',
        component: () => import('@v/aps/ApsBom/ApsBomIndex.vue'),
        meta: {
          title: '零件',
          elIcon: 'Setting',
        },
      },
      {
        path: '/aps/ApsBomGroup',
        name: '零件组',
        component: () => import('@v/aps/ApsBomGroup/ApsBomGroupIndex.vue'),
        meta: {
          title: '零件组',
          elIcon: 'Files',
        },
      },
      {
        path: '/aps/ApsGoods',
        name: '商品',
        component: () => import('@v/aps/ApsGoods/ApsGoodsIndex.vue'),
        meta: {
          title: '商品',
          elIcon: 'List',
        },
      },
      {
        path: '/aps/ApsGoodsBom',
        name: '商品零件',
        component: () => import('@v/aps/ApsGoodsBom/ApsGoodsBomIndex.vue'),
        meta: {
          title: '商品零件',
          elIcon: 'SwitchFilled',
        },
      },
      {
        path: '/aps/ApsProjectConfig',
        name: '制造配置',
        component: () =>
          import('@/view/aps/ApsProjectConfig/ApsProjectConfigIndex.vue'),
        meta: {
          title: '制造配置',
          elIcon: 'Money',
        },
      },
      {
        path: '/aps/ApsSaleConfig',
        name: '销售配置',
        component: () =>
          import('@/view/aps/ApsSaleConfig/ApsSaleConfigIndex.vue'),
        meta: {
          title: '销售配置',
          elIcon: 'MagicStick',
        },
      },
      {
        path: '/aps/ApsGoodsSaleProjectConfig',
        name: '商品销售制造配置',
        component: () =>
          import('@/view/aps/ApsGoodsSaleProjectConfig/ApsGoodsSaleProjectConfigIndex.vue'),
        meta: {
          title: '商品销售制造配置',
          elIcon: 'ScaleToOriginal',
        },
      },
      {
        path: '/aps/ApsGoodsSaleItem',
        name: '商品销售配置',
        component: () =>
          import('@/view/aps/ApsGoodsSaleItem/ApsGoodsSaleItemIndex.vue'),
        meta: {
          title: '商品销售配置',
          elIcon: 'SetUp',
        },
      },
      {
        path: '/aps/ApsGoodsBomBuyPlan',
        name: '零件购买计划',
        component: () =>
          import('@/view/aps/ApsGoodsBomBuyPlan/ApsGoodsBomBuyPlanIndex.vue'),
        meta: {
          title: '零件购买计划',
          elIcon: 'ShoppingCart',
        },
      },
    ],
  },
  {
    path: '/base/forecast',
    name: '预测信息',
    redirect: '',
    meta: {
      title: '预测信息',
      elIcon: 'Monitor',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsGoodsForecast/forecast/:id',
        name: '商品预测数据',
        component: () =>
          import('@/view/aps/ApsGoodsForecast/ApsGoodsForecastData.vue'),
        meta: {
          title: '商品预测数据',
          elIcon: 'DataLine',
          hidden: true,
        },
      },
      {
        path: '/aps/ApsGoodsForecast',
        name: '商品预测',
        component: () =>
          import('@/view/aps/ApsGoodsForecast/ApsGoodsForecastIndex.vue'),
        meta: {
          title: '商品预测',
          elIcon: 'DataLine',
        },
      },
      {
        path: '/aps/ApsGoodsForecastMain',
        name: '预测主版本',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMain/ApsGoodsForecastMainIndex.vue'),
        meta: {
          title: '预测主版本',
          elIcon: 'Monitor',
        },
      },
      {
        path: '/aps/ApsGoodsForecastMake',
        name: '制造版本',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMake/ApsGoodsForecastMakeIndex.vue'),
        meta: {
          title: '制造版本',
          elIcon: 'SuitcaseLine',
        },
      },
      {
        path: '/aps/ApsGoodsForecastMainMake',
        name: '制造主版本',
        component: () =>
          import('@/view/aps/ApsGoodsForecastMainMake/ApsGoodsForecastMainMakeIndex.vue'),
        meta: {
          title: '制造主版本',
          elIcon: 'Film',
        },
      },
    ],
  },
  {
    path: '/base/order',
    name: '订单信息',
    redirect: '',
    meta: {
      title: '订单信息',
      elIcon: 'ShoppingCartFull',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsOrderFieldShowTemplate',
        name: '订单显示模板',
        component: () =>
          import('@/view/aps/ApsOrderFieldShowTemplate/ApsOrderFieldShowTemplateIndex.vue'),
        meta: {
          title: '订单显示模板',
          elIcon: 'Coin',
        },
      },
      {
        path: '/aps/ApsOrder',
        name: '订单',
        component: () => import('@/view/aps/ApsOrder/ApsOrderIndex.vue'),
        meta: {
          title: '订单',
          elIcon: 'ShoppingCartFull',
        },
      },
      {
        path: '/aps/ApsOrder/TimeLine',
        name: '订单状态时间',
        component: () => import('@/view/aps/ApsOrder/TimeLine.vue'),
        meta: {
          title: '订单状态时间',
          elIcon: 'Timer',
        },
      },
      {
        path: '/aps/ApsOrderGoodsHistory',
        name: '订单商品统计',
        component: () =>
          import('@/view/aps/ApsOrderGoodsHistory/ApsOrderGoodsHistoryIndex.vue'),
        meta: {
          title: '订单商品统计',
          elIcon: 'Histogram',
        },
      },
      {
        path: '/aps/ApsOrderGoodsSaleConfigHistory',
        name: '订单商品销售统计',
        component: () =>
          import('@/view/aps/ApsOrderGoodsSaleConfigHistory/ApsOrderGoodsSaleConfigHistoryIndex.vue'),
        meta: {
          title: '订单商品销售统计',
          elIcon: 'Histogram',
        },
      },
      {
        path: '/aps/ApsRollingForecastFactoryCapacity',
        name: '工厂滚动产能',
        component: () =>
          import('@/view/aps/ApsRollingForecastFactoryCapacity/ApsRollingForecastFactoryCapacityIndex.vue'),
        meta: {
          title: '工厂滚动产能',
          elIcon: 'DataLine',
          hidden: true,
        },
      },
    ],
  },
  {
    path: '/base/make',
    name: '产能信息',
    redirect: '',
    meta: {
      title: '产能信息',
      elIcon: 'SetUp',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsMakeCapacitySaleConfig',
        name: '销售产能',
        component: () =>
          import('@/view/aps/ApsMakeCapacitySaleConfig/ApsMakeCapacitySaleConfigIndex.vue'),
        meta: {
          title: '销售产能',
          elIcon: 'SetUp',
        },
      },
      {
        path: '/aps/ApsMakeCapacityGoods',
        name: '商品产能',
        component: () =>
          import('@/view/aps/ApsMakeCapacityGoods/ApsMakeCapacityGoodsIndex.vue'),
        meta: {
          title: '商品产能',
          elIcon: 'List',
        },
      },
      {
        path: '/aps/ApsMakeCapacityFactory',
        name: '工厂产能',
        component: () =>
          import('@/view/aps/ApsMakeCapacityFactory/ApsMakeCapacityFactoryIndex.vue'),
        meta: {
          title: '工厂产能',
          elIcon: 'OfficeBuilding',
        },
      },
    ],
  },
  {
    path: '/base/scheduling',
    name: '排产排程',
    redirect: '',
    meta: {
      title: '排产排程',
      elIcon: 'Files',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsSchedulingConstraints',
        name: '排产约束',
        component: () =>
          import('@/view/aps/ApsSchedulingConstraints/ApsSchedulingConstraintsIndex.vue'),
        meta: {
          title: '排产约束',
          elIcon: 'ShoppingBag',
        },
      },
      {
        path: '/aps/ApsSchedulingVersion',
        name: '排产',
        component: () =>
          import('@/view/aps/ApsSchedulingVersion/ApsSchedulingVersionIndex.vue'),
        meta: {
          title: '排产',
          elIcon: 'Refrigerator',
        },
      },
      {
        path: '/aps/ApsSchedulingDayConfig',
        name: '排程配置',
        component: () =>
          import('@/view/aps/ApsSchedulingDayConfig/ApsSchedulingDayConfigIndex.vue'),
        meta: {
          title: '排程配置',
          elIcon: 'Suitcase',
        },
      },
      {
        path: '/aps/ApsSchedulingDayConfigVersion',
        name: '排程版本',
        component: () =>
          import('@/view/aps/ApsSchedulingDayConfigVersion/ApsSchedulingDayConfigVersionIndex.vue'),
        meta: {
          title: '排程版本',
          elIcon: 'Files',
        },
      },
    ],
  },
  {
    path: '/base/kitting',
    name: '齐套管理',
    redirect: '',
    meta: {
      title: '齐套管理',
      elIcon: 'Money',
    },
    component: Layouts,
    children: [
      {
        path: '/aps/ApsOrderGoodsBomKittingTemplate',
        name: '齐套字段模板',
        component: () =>
          import('@/view/aps/ApsOrderGoodsBomKittingTemplate/ApsOrderGoodsBomKittingTemplateIndex.vue'),
        meta: {
          title: '齐套字段模板',
          elIcon: 'Files',
        },
      },
      {
        path: '/aps/ApsOrderGoodsBomKittingVersion',
        name: '齐套检查版本',
        component: () =>
          import('@/view/aps/ApsOrderGoodsBomKittingVersion/ApsOrderGoodsBomKittingVersionIndex.vue'),
        meta: {
          title: '齐套检查版本',
          elIcon: 'Files',
        },
      },
      {
        path: '/aps/ApsOrderGoodsBomKittingVersion/:bizKey',
        name: '齐套版本',
        component: () =>
          import('@/view/aps/ApsOrderGoodsBomKittingVersion/ApsOrderGoodsBomKittingVersionIndex.vue'),
        meta: {
          title: '齐套版本',
          elIcon: 'Files',
          hidden: true,
        },
      },
    ],
  },
];

export function menuList(): RouteRecordRaw[] {
  return allMenuList;
}
