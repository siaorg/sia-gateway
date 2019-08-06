const ConnecteTestPage = resolve => require(['../views/connecte-test.page.vue'], resolve)
const ConnecteTestModuleRouter = {}
ConnecteTestModuleRouter.routers = [
  {
    path: '/router-connecte-test',
    component: ConnecteTestPage,
    name: 'ConnecteTestPage',
    meta: {
      title: '路由连通性测试',
      auth: 'show'
    }
  }
]
export default ConnecteTestModuleRouter
