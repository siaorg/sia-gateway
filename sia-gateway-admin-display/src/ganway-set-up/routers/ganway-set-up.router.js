const GanwaySetUpPage = resolve => require(['../views/ganway-set-up.page.vue'], resolve)
const GanwaySetUpModuleRouter = {}
GanwaySetUpModuleRouter.routers = [
  {
    path: '/gateway-set-up',
    component: GanwaySetUpPage,
    name: 'GanwaySetUpPage',
    meta: {
      title: '网关设置',
      auth: 'show'
    }
  }
]
export default GanwaySetUpModuleRouter
