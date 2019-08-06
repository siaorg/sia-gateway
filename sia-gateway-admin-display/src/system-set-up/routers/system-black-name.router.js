const SystemBlackNamePage = resolve => require(['../views/system-black-name.page.vue'], resolve)
const SystemSetUpModuleRouter = {}
SystemSetUpModuleRouter.routers = [
  {
    path: '/system-black-name',
    component: SystemBlackNamePage,
    name: 'SystemBlackNamePage',
    meta: {
      title: '系统黑名单',
      auth: 'show'
    }
  }
]
export default SystemSetUpModuleRouter
