<template>
  <div class="dispatch-system-default frame">
    <left-menu-tmpl></left-menu-tmpl>
    <div class="frame-container">
      <breadcrumb-tmpl @showKeepLiveList="showKeepLiveList"></breadcrumb-tmpl>
      <transition name="page-fade" mode="out-in">
        <keep-alive :include="tagsList">
          <router-view></router-view>
        </keep-alive>
      </transition>
    </div>
  </div>
</template>
<script>
const LeftMenuTmpl = resolve => require(['../../common/components/left-menu.tmpl'], resolve)
const BreadcrumbTmpl = resolve => require(['../../common/components/breadcrumb.tmpl'], resolve)
export default {
  name: 'FrameIndexPage',
  data () {
    return {
      tagsList: []
    }
  },
  components: {
    LeftMenuTmpl,
    BreadcrumbTmpl
  },
  methods: {
    showKeepLiveList: function (msg) {
      let arr = []
      for (let i = 0, len = msg.length; i < len; i++) {
        if (msg[i].name && msg[i].name === 'RouteCreatePage') {
          arr.push(msg[i].name)
        }
      }
      this.tagsList = arr
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/index.page.less";
</style>
<style lang="less">
@import '../../common/styles/transition.less';
@import '../../common/styles/theme.less'; // [ext] inject config.less as global setting
</style>
