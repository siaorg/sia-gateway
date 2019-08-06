const GroupManageListPage = resolve => require(['../views/group-manage-list.page.vue'], resolve)
const GroupDetailPage = resolve => require(['../views/group-detail.page.vue'], resolve)
const TaskManageModuleRouter = {}
TaskManageModuleRouter.routers = [
  {
    path: '/group-manage-list',
    component: GroupManageListPage,
    name: 'GroupManageListPage',
    meta: {
      title: '组件管理',
      auth: 'show'
    }
  },
  {
    path: '/group-detail',
    component: GroupDetailPage,
    name: 'GroupDetailPage',
    meta: {
      title: '组件详情',
      auth: 'show',
      parentnode: {'组件管理': '/group-manage-list'}
    }
  }
]
export default TaskManageModuleRouter
