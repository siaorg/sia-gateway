const GatewayMonitorPage = resolve => require(['../views/gateway-monitor.page.vue'], resolve)
const GatewayLogPage = resolve => require(['../views/gateway-log-list.page.vue'], resolve)
const MonitorListPage = resolve => require(['../views/monitor-list.page.vue'], resolve)
const GatewayMonitorModuleRouter = {}
GatewayMonitorModuleRouter.routers = [
  {
    path: '/gateway-monitor',
    component: GatewayMonitorPage,
    name: 'GatewayMonitorPage',
    meta: {
      title: '网关监控',
      auth: 'show'
    }
  },
  {
    path: '/gateway-log-list',
    component: GatewayLogPage,
    name: 'GatewayLogPage',
    meta: {
      title: '日志列表',
      auth: 'show'
    }
  },
  {
    path: '/monitor-list',
    component: MonitorListPage,
    name: 'MonitorListPage',
    meta: {
      title: '监控列表',
      auth: 'show'
    }
  }
]
export default GatewayMonitorModuleRouter
