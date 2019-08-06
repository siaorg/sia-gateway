const gatewayAuditListPage = resolve => require(['../views/gateway-audit-list.page.vue'], resolve)
const GatewayAuditModuleRouter = {}
GatewayAuditModuleRouter.routers = [
  {
    path: '/gateway-audit',
    component: gatewayAuditListPage,
    name: 'gatewayAuditListPage',
    meta: {
      title: '路由管理',
      auth: 'show'
    }
  }
]
export default GatewayAuditModuleRouter
