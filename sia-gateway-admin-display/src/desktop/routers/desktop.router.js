const DeskTopPage = resolve => require(['../views/desktop.page.vue'], resolve)
const DeskTopModuleRouter = {}
DeskTopModuleRouter.routers = [
  {
    path: '/desktop',
    component: DeskTopPage,
    name: 'DeskTopPage',
    meta: {
      title: 'DeskTop',
      auth: 'notShow'
    }
  }
]
export default DeskTopModuleRouter
