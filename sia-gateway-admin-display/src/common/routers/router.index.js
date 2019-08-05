import Vue from 'vue'
import VueRouter from 'vue-router'
import CommonRouter from './common.router.js'
import FrameRouter from '../../frame/routers/frame.router.js'
import LoginRouter from '../../login/routers/login.router.js'

Vue.use(VueRouter)

let routerArray = []

routerArray = routerArray.concat(CommonRouter.routers)
routerArray = routerArray.concat(FrameRouter.routers)
routerArray = routerArray.concat(LoginRouter.routers)

const appRouter = new VueRouter({
  mode: 'history',
  saveScrollPosition: true,
  routes: routerArray
})

// 路由跳转拦截
appRouter.beforeEach((to, from, next) => {
  if (to.meta.auth) {
    if (sessionStorage.getItem('login') === 'show') {
      next()
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    }
  } else {
    next()
  }
  // next()
  document.title = to.meta.title
})

export default appRouter
