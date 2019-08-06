const RouteManageListPage = resolve => require(['../views/route-manage-list.page.vue'], resolve)
const RouteCreatePage = resolve => require(['../views/route-create.page.vue'], resolve)
const RouteEditPage = resolve => require(['../views/route-edit.page.vue'], resolve)
const BlackWhiteDetailPage = resolve => require(['../views/black-white-detail.page.vue'], resolve)
const RunStaticDetailPage = resolve => require(['../views/run-static-detail.page.vue'], resolve)
const RouteSafeAuthPage = resolve => require(['../views/route-safe-auth.page.vue'], resolve)
const TaskManageModuleRouter = {}
TaskManageModuleRouter.routers = [
  {
    path: '/route-manage-list',
    component: RouteManageListPage,
    name: 'RouteManageListPage',
    meta: {
      title: '路由管理',
      auth: 'show'
    }
  },
  {
    path: '/route-create',
    component: RouteCreatePage,
    name: 'RouteCreatePage',
    meta: {
      title: '新建路由',
      auth: 'show',
      parentnode: {'路由管理': '/route-manage-list'}
    }
  },
  {
    path: '/route-edit',
    component: RouteEditPage,
    name: 'RouteEditPage',
    meta: {
      title: '修改路由信息',
      auth: 'show',
      parentnode: {'路由管理': '/route-manage-list'}
    }
  },
  {
    path: '/black-white-detail',
    component: BlackWhiteDetailPage,
    name: 'BlackWhiteDetailPage',
    meta: {
      title: '黑白名单信息',
      auth: 'show',
      parentnode: {'路由管理': '/route-manage-list'}
    }
  },
  {
    path: '/run-static-detail',
    component: RunStaticDetailPage,
    name: 'RunStaticDetailPage',
    meta: {
      title: '运行统计',
      auth: 'show',
      parentnode: {'路由管理': '/route-manage-list'}
    }
  },
  {
    path: '/route-safe-auth',
    component: RouteSafeAuthPage,
    name: 'RouteSafeAuthPage',
    meta: {
      title: '安全认证',
      auth: 'show',
      parentnode: {'路由管理': '/route-manage-list'}
    }
  }
]
export default TaskManageModuleRouter
