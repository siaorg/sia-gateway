const GatewaySwaggerPage = resolve => require(['../views/gateway-swagger.page.vue'], resolve)
const GatewaySwaggerModuleRouter = {}
GatewaySwaggerModuleRouter.routers = [
  {
    path: '/gateway-swagger',
    component: GatewaySwaggerPage,
    name: 'GatewaySwaggerPage',
    meta: {
      title: '网关swagger',
      auth: 'notShow'
    }
  }
]
export default GatewaySwaggerModuleRouter
