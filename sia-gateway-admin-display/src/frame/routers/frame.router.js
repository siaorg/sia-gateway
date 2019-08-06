import RouteManageModuleRouter from '../../route-manage/routers/route-manage.router.js'
import ConnecteTestModuleRouter from '../../connecte-test/routers/connecte-test.router.js'
import GruopManageModuleRouter from '../../group-manage/routers/group-manage.router.js'
import HomeModuleRouter from '../../home/routers/home.router'
import LogManageModuleRouter from '../../log-manage/routers/log-manage.router'
import SystemSetUpModuleRouter from '../../system-set-up/routers/system-black-name.router'
import GatewaySwaggerModuleRouter from '../../gateway-swagger/routers/gateway-monitor.router.js'
import GatewayMonitorModuleRouter from '../../gateway-monitor/routers/gateway-monitor.router'
import FuseManageModuleRouter from '../../fuse-manage/routers/fuse-manage.router'
import GatewaySetUpModuleRouter from '../../ganway-set-up//routers/ganway-set-up.router'
import GatewayAuditModuleRouter from '../../gateway-audit/routers/gateway-audit.router'
import GatewayEurekaModuleRouter from '../../gateway-eureka/routers/gateway-eureka.router'
import DeskTopModuleRouter from '../../desktop/routers/desktop.router'
const FrameIndexPage = resolve => require(['../views/index.page'], resolve)

const frameRouter = {}

// sys module
let _routerArray = [{
  path: '/',
  redirect: '/desktop'
}]

_routerArray = _routerArray.concat(HomeModuleRouter.routers) // 首页
_routerArray = _routerArray.concat(LogManageModuleRouter.routers) // 日志管理
_routerArray = _routerArray.concat(RouteManageModuleRouter.routers) // 路由管理
_routerArray = _routerArray.concat(ConnecteTestModuleRouter.routers) // 路由连通性测试
_routerArray = _routerArray.concat(GruopManageModuleRouter.routers) // 组件管理
_routerArray = _routerArray.concat(SystemSetUpModuleRouter.routers) // 系统黑名单
_routerArray = _routerArray.concat(GatewayMonitorModuleRouter.routers) // 网关管理
_routerArray = _routerArray.concat(FuseManageModuleRouter.routers) // 熔断管理
_routerArray = _routerArray.concat(GatewaySetUpModuleRouter.routers) // 网关设置
_routerArray = _routerArray.concat(GatewaySwaggerModuleRouter.routers) // 网关swagger
_routerArray = _routerArray.concat(GatewayAuditModuleRouter.routers) // 网关审计
_routerArray = _routerArray.concat(GatewayEurekaModuleRouter.routers) // 网关注册中心
_routerArray = _routerArray.concat(DeskTopModuleRouter.routers) // desktop
frameRouter.routers = [
  {
    path: '/',
    component: FrameIndexPage,
    hidden: true,
    meta: {
      title: '网关管理中心',
      auth: 'notshow',
      access: {module: 'frame', page: 'index'}
    },
    children: _routerArray
  }
]

export default frameRouter
