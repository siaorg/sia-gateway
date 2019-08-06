const gatewayEurekaListPage = resolve => require(['../views/gateway-eureka-list.page.vue'], resolve)
const GatewayEurekaModuleRouter = {}
GatewayEurekaModuleRouter.routers = [
  {
    path: '/gateway-eureka',
    component: gatewayEurekaListPage,
    name: 'gatewayEurekaListPage',
    meta: {
      title: '网关注册中心',
      auth: 'show'
    }
  }
]
export default GatewayEurekaModuleRouter
