const LogManageListPage = resolve => require(['../views/log-manage-list.page.vue'], resolve)
const logManageModuleRouter = {}
logManageModuleRouter.routers = [
  {
    path: '/log-manage-list',
    component: LogManageListPage,
    name: 'LogManageListPage',
    meta: {
      title: '日志管理',
      auth: 'show'
    }
  }
]
export default logManageModuleRouter
