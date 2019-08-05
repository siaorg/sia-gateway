<template>
    <div class="left-menu">
      <div class="logo">
        <span><img src="../images/logo.png" alt=""></span>
        <span>网关系统</span>
      </div>
      <el-menu :default-active="onRoutes()" class="el-menu-vertical" @select="handleSelectMenu">
          <el-menu-item index="desktop">DeskTop</el-menu-item>
          <el-menu-item index="home">首页</el-menu-item>
          <el-menu-item index="route-manage-list">路由管理</el-menu-item>
          <el-menu-item index="group-manage-list">组件管理</el-menu-item>
          <el-menu-item index="gateway-monitor">网关监控</el-menu-item>
          <el-menu-item index="log-manage-list">日志管理</el-menu-item>
          <el-menu-item index="fuse-manage">熔断管理</el-menu-item>
          <el-menu-item index="gateway-eureka">注册中心管理</el-menu-item>
          <el-menu-item index="router-connecte-test">路由联通性测试</el-menu-item>
          <el-menu-item index="gateway-swagger">网关Swagger</el-menu-item>
          <el-menu-item index="system-black-name">系统黑名单</el-menu-item>
          <el-menu-item index="gateway-audit">网关审计</el-menu-item>
          <el-menu-item index="gateway-set-up">网关设置</el-menu-item>
      </el-menu>
    </div>
</template>

<script>
export default {
  name: 'LeftMenuTmpl',
  methods: {
    onRoutes: function () {
      return this.$route.path.replace('/', '')
    },
    handleSelectMenu: function (index) {
      if (sessionStorage.getItem('setGatewayGroup') !== 'show' && index !== 'desktop'  && index !== 'gateway-swagger') {
        this.$alert('请先在DeskTop页面选择网关组！', '警告', {
          confirmButtonText: '确定',
          type: 'warning'
        })
        return false
      }
      switch (index) {
        case 'desktop':
          this.$router.push('/desktop')
          break
        case 'home':
          this.$router.push('/home')
          break
        case 'route-manage-list':
          this.$router.push('/route-manage-list')
          break
        case 'router-connecte-test':
          this.$router.push('/router-connecte-test')
          break
        case 'group-manage-list':
          this.$router.push('/group-manage-list')
          break
        case 'log-manage-list':
          this.$router.push({path: '/log-manage-list'})
          break
        case 'system-black-name':
          this.$router.push({path: '/system-black-name'})
          break
        case 'gateway-swagger':
          this.$router.push({path: '/gateway-swagger'})
          break
        case 'gateway-monitor':
          this.$router.push({path: '/gateway-monitor'})
          break
        case 'fuse-manage':
          this.$router.push({path: '/fuse-manage'})
          break
        case 'gateway-set-up':
          this.$router.push({path: '/gateway-set-up'})
          break
        case 'gateway-audit':
          this.$router.push({path: '/gateway-audit'})
          break
        case 'gateway-eureka':
          this.$router.push({path: '/gateway-eureka'})
          break
      }
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/left-menu.tmpl.less';
</style>
<style lang="less" scoped>
@import '../styles/transition.less';
</style>
