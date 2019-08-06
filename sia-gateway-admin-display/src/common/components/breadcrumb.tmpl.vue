<template>
  <div class="bread-crumb">
    <div class="frame-breadcrumb">
      <div class="left-icon" @click="handleClickMoveRight"><span class="el-icon-arrow-left"></span></div>
      <div class="current-router">
        {{$route.meta.title}}
      </div>
      <transition-group class="bread-crumb-main" ref='breadCrumbMainStyle'>
        <ul class="bread-crumb-list" key="home" ref="breadCrumbListStyle">
          <li class="tags-li" v-for="(item,index) in tagsList" :class="{'active': isActive(item.title)}" :key="index">
              <router-link :to="item.path">
                  {{item.title}}
              </router-link>
              <span @click="closeTags(index)"><i class="el-icon-close"></i></span>
          </li>
        </ul>
      </transition-group>
      <div class="right-icon" @click="handleClickMoveLeft"><span class="el-icon-arrow-right"></span></div>
    </div>
    <div class="current-gateway">
      <div class="gateway-icon">
        <img src="../../common/images/current-gateway-icon.svg" alt="">
      </div>
      <div class="gateway-name">
        <el-popover
          placement="top-start"
          width="50"
          popper-class="logout-box"
          trigger="hover">
          <div class="logout-text" @click="logoutClick">退出</div>
          <div class="curren-grounp" slot="reference">
            <span>当前网关组</span>
            <span :title="$store.state.frame.currentGatewayGroup">{{$store.state.frame.currentGatewayGroup ? $store.state.frame.currentGatewayGroup : '请先选择网关组'}}</span>
          </div>
        </el-popover>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      levelList: null,
      tagsList: []
    }
  },
  watch: {
    $route (newValue, oldValue) {
      this.getBreadcrumb(newValue)
    }
  },
  created () {
    this.getBreadcrumb(this.$route)
  },
  methods: {
    // 退出登录
    logoutClick (path) {
      sessionStorage.removeItem('login')
      this.$router.push({path: '/login'})
    },
    isActive (path) {
      return path === this.$route.meta.title
    },
    getBreadcrumb (route) {
      const isExist = this.tagsList.some(item => {
        return item.title === route.meta.title
      })
      if (!isExist) {
        this.tagsList.push({
          title: route.meta.title,
          path: route.fullPath,
          name: route.matched[1].components.default.name
        })
      }
      this.$emit('showKeepLiveList', this.tagsList)
    },
    // 关闭单个标签
    closeTags: function (index) {
      const delItem = this.tagsList.splice(index, 1)[0]
      const item = this.tagsList[index] ? this.tagsList[index] : this.tagsList[index - 1]
      if (item) {
        delItem.path === this.$route.fullPath && this.$router.push(item.path)
      } else {
        this.$router.push('/')
      }
    },
    handleClickMoveLeft: function () {
      console.log(this.$refs.breadCrumbListStyle, this.$refs.breadCrumbMainStyle)
      let breadCrumbListStyle = parseInt(this.getStyle(this.$refs.breadCrumbListStyle, 'width'))
      let breadCrumbMainStyle = parseInt(this.getStyle(this.$refs.breadCrumbMainStyle.$el, 'width'))
      if (breadCrumbListStyle <= breadCrumbMainStyle) {
        return
      }
      let dis = parseInt(this.getStyle(this.$refs.breadCrumbListStyle, 'left')) - 200
      let minWidth = breadCrumbListStyle - breadCrumbMainStyle
      if (dis <= -minWidth) {
        dis = -minWidth
      }
      this.$refs.breadCrumbListStyle.style.left = dis + 'px'
    },
    handleClickMoveRight: function () {
      let breadCrumbListStyle = parseInt(this.getStyle(this.$refs.breadCrumbListStyle, 'width'))
      let breadCrumbMainStyle = parseInt(this.getStyle(this.$refs.breadCrumbMainStyle.$el, 'width'))
      if (breadCrumbListStyle <= breadCrumbMainStyle && parseInt(this.getStyle(this.$refs.breadCrumbListStyle, 'left')) > 0) {
        return
      }
      let dis = parseInt(this.getStyle(this.$refs.breadCrumbListStyle, 'left')) + 200
      if (dis > 0) {
        dis = 0
      }
      this.$refs.breadCrumbListStyle.style.left = dis + 'px'
    },
    getStyle (obj, attr) {
      return obj.currentStyle ? obj.currentStyle[attr] : getComputedStyle(obj)[attr]
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/breadcrumb.tmpl.less';
@import '../styles/transition.less';
</style>

