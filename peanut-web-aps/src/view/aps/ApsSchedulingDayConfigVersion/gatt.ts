import {gantt} from 'dhtmlx-gantt';

var hourToStr = gantt.date.date_to_str('%i');
const hourRangeFormat = (step) => {
  return function (date) {
    var intervalEnd = new Date(gantt.date.add(date, step, 'minute') - 1);
    return hourToStr(date) + ' - ' + hourToStr(intervalEnd);
  };
};

export const zoomConfig = {
  levels: [
    {
      name: 'month',
      label: '月',
      scale_height: 60,
      min_column_width: 20,
      timeInterval: 86400 * 31,
      scales: [
        {unit: 'year', format: '%Y'},
        {unit: 'month', format: '%m'},
      ],
    },
    {
      name: 'day',
      label: '天',
      scale_height: 60,
      min_column_width: 30,
      timeInterval: 86400,
      scales: [
        {unit: 'month', format: '%Y-%m'},
        {unit: 'day', format: '%d'},
      ],
    },
    {
      name: 'hour',
      label: '小时',
      scale_height: 60,
      min_column_width: 30,
      timeInterval: 3600,
      scales: [
        {unit: 'month', format: '%Y-%m'},
        {unit: 'day', format: '%d'},
        {unit: 'hour', format: '%H'},
      ],
    },
    {
      name: 'minute',
      label: '分钟',
      scale_height: 60,
      min_column_width: 60,
      timeInterval: 1800,
      scales: [
        {unit: 'month', format: '%Y-%m'},
        {unit: 'day', format: '%d'},
        {unit: 'hour', format: '%H'},
        {unit: 'minute', step: 30, format: hourRangeFormat(30)},
      ],
    },
  ],
};

export function initGantt(
  gantt: any,
  zoomConfig: any,
  zoomLevel: any,
  ganttDiv: string = 'gantt_here'
) {
  gantt.plugins({
    auto_scheduling: true,
    click_drag: true,
    critical_path: true,
    drag_timeline: true,
    fullscreen: true,
    keyboard_navigation: false,
    quick_info: false,
    tooltip: true,
    undo: true,
    grouping: true,
    marker: true,
    multiselect: false,
    overlay: true,
    export_api: true,
  });
  gantt.config.readonly = false; //是否只读
  gantt.config.lightbox.sections = []; // 清空弹窗内容
  gantt.config.drag_move = true; //允许拖动任务时移动任务
  gantt.config.auto_types = true; //将包含子任务的任务转换为项目，将没有子任务的项目转换回任务
  gantt.config.open_split_tasks = true; //激活列表展开、折叠功能
  gantt.config.xml_date = '%Y.%m.%d'; //甘特图时间数据格式
  gantt.config.show_errors = false; //不显示错误信息
  gantt.ext.zoom.init(zoomConfig); //配置初始化扩展
  gantt.i18n.setLocale('cn');
  gantt.config.work_time = true;
  console.info('zoomLevel.value ', zoomLevel.value);
  gantt.ext.zoom.setLevel(zoomLevel.value); //切换到指定的缩放级别x
  gantt.config.add_column = false; //添加符号
  gantt.config.show_progress = false; //显示进度条
  gantt.config.drag_resize = true; //允许拖动任务时调整任务大小
  gantt.config.drag_links = false; //允许拖动任务时调整任务链接
  gantt.config.order_branch = true; //允许拖动任务时保持任务顺序
  gantt.config.drag_progress = true; //允许拖动任务时调整任务进度

  gantt.config.columns = [
    {
      name: 'machineName', //tasks参数名
      tree: true, //是否开始tree联级
      width: '170', //宽度 值为string 例如 width:"75" "*" 为auto
      resize: true, //可重置大小
      label: '订单', //标签名
      template: function (obj) {
        return obj.machineName;
      }, //返回值
      align: 'left', //对齐方式
    },
    {
      name: 'start_date',
      label: '开始时间',
      width: '210',
      align: 'left',
    },
    {
      name: 'end_date',
      label: '结束时间',
      width: '210',
      align: 'left',
    },
  ];

  // 设置当天标记
  const dateToStr = gantt.date.date_to_str(gantt.config.task_date);
  const today = new Date(new Date().setHours(0, 0, 0, 0)); // 获取当天零点的时间
  gantt.addMarker({
    start_date: today,
    css: 'today',
    text: '今日',
    title: `Today: ${dateToStr(today)}`,
  });
  // 设置颜色
  gantt.templates.task_class = function (start, end, task) {
    if (task.estimate) {
      return 'gray_color';
    } else {
      return 'red_color';
    }
  };
  gantt.config.work_time = true;
  gantt.templates.timeline_cell_class = function (task, date) {
    if (date.getDay() === 0 || date.getDay() === 6) {
      // 检查是否为周末
      return 'weekend-border-bottom '; // 返回红色样式
    } else {
      return ''; // 其他日期使用默认样式
    }

    const targetDate = new Date(2025, 10, 27); // 2024-12-17
    if (date.getTime() === targetDate.getTime()) {
      return 'red_color'; // 返回红色样式
    }
    return ''; // 其他日期使用默认样式
  };
  gantt.attachEvent('onTaskDblClick', function (id, e) {
    console.info('onTaskDblClick', id, e);
  });
  gantt.config.fit_tasks = true; //自动适配任务大小

  gantt.templates.tooltip_text = function (start, end, task) {
    return (
      task.machineName +
      '<br/><strong>开始:</strong>' +
      gantt.templates.tooltip_date_format(start) +
      '<br/><strong>结束:</strong>' +
      gantt.templates.tooltip_date_format(end)
    );
  };

  gantt.init(ganttDiv);
}
