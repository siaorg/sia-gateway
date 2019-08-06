const fuseManagePage = resolve => require(['../views/fuse-manage.page.vue'], resolve)
const FuseManageModuleRouter = {}
FuseManageModuleRouter.routers = [
  {
    path: '/fuse-manage',
    component: fuseManagePage,
    name: 'fuseManagePage',
    meta: {
      title: '路由管理',
      auth: 'show'
    }
  }
]
export default FuseManageModuleRouter
