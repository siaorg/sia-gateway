<template>
  <div class="dispatch-system-default desktop-page scroll-bar">
    <div class="gateway-group" 
      v-for="(item,index) in grounpGatewayList" 
      @click="toHomePage(item, $event)" 
      :key="index"
      :class="{'active': item.zuulGroupName === $store.state.frame.currentGatewayGroup}">
      <h3>{{item.zuulGroupName}}</h3>
      <ul>
        <li>
          <span>角色值：</span><span>{{item.zuulGroupName?item.zuulGroupName.split('-')[0] : ''}}</span>
        </li>
        <li>
          <span>拥有实例：</span><span>{{item.instanceNo}}</span>
        </li>
        <li>
          <span>网关状态：</span><span>{{item.zuulStatus}}</span>
        </li>
        <li>
          <span>网关描述：</span>
          <span>{{item.zuulDesc}} <i class="el-icon-edit"></i></span>
        </li>
      </ul>
    </div>
    <!-- 路由组件  start -->
    <edit-desc-tmpl  v-if="isShowEditTmpl" :zuulGroup="zuulGroup" v-on:showHiddenEditDescTmpl="showHiddenEditDescTmpl">
    </edit-desc-tmpl>
    <!-- 路由组件  end -->
  </div>
</template>

<script>
const editDescTmpl = resolve => require(['../components/edit-desc.tmpl.vue'], resolve)
export default {
  name: 'DeskTopPage',
  components: {editDescTmpl},
  data () {
    return {
      loadingRefresh: false,
      grounpGatewayList: [],
      isShowEditTmpl: false,
      zuulGroup: {}
    }
  },
  created () {
    this.getGatewayGroup()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    showHiddenEditDescTmpl: function (val) {
      this.isShowEditTmpl = val
      this.getGatewayGroup()
    },
    toHomePage: function (val, event) {
      let self = this
      if (event.target.tagName === 'I') {
        this.zuulGroup = val
        this.isShowEditTmpl = true
        return false
      }
      let prams = {
        currentRole: val.zuulGroupName.split('-')[0]
      }
      self.$http.post(self.$api.getApiAddress('setCurrentGroupName', 'CESHI_API_HOST'), prams).then((res) => {
        if (res.data.code === 200) {
          this.$router.push({path: '/home'})
          sessionStorage.setItem('setGatewayGroup', 'show')
          this.$store.dispatch('CURREN_GATEWAY_GROUP_ACTION', val.zuulGroupName)
        } else {
          self.$message({message: '该网管组不存在', type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    getGatewayGroup: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('getMutiGroupNames', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          this.grounpGatewayList = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/desktop.page.less";
</style>
<style lang="less">
@import "../styles/desktop.page.reset.less";
</style>
