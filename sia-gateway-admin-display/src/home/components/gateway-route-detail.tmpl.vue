<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>路由详情</span>
          <i class="close-icon" @click="showHiddeRouterDetail"></i>
        </div>
        <div class="info">
          <p class="router-list" v-if="viewDetailObj!==''">
            <span v-for="(item, index) in viewDetailObj" :key="index" v-show="item!==''">{{item}}</span>
          </p>
          <p class="router-list" v-else>
            暂无数据
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['routerDetailParams'],
  data () {
    return {
      viewDetailObj: {}
    }
  },
  created () {
    this.getTaskList()
  },
  methods: {
    getTaskList: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/validrouteCheck', 'CESHI_API_HOST'), {
        zuulInstanceId: self.routerDetailParams
      }).then((res) => {
        this.viewDetailObj = res.data !== 'null' ? res.data.split(';') : ''
      })
    },
    showHiddeRouterDetail: function () {
      this.$emit('showHiddeRouterDetail', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/gateway-status-detail.tmpl.less';
</style>
