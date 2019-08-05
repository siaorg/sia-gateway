<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>监控中心状态详情</span>
          <i class="close-icon" @click="showHiddenMonitor"></i>
        </div>
        <div class="info">
          <p>
            <span>主机名称：</span>
            <span class="msg">{{viewDetailObj.adminHotsName}}</span>
          </p>
          <p>
            <span>实例ID：</span>
            <span class="msg">{{viewDetailObj.adminInstanceid}}</span>
          </p>
          <p>
            <span>状态：</span>
            <span class="msg">{{viewDetailObj.adminStatus}}</span>
          </p>
          <p>
            <span>最后一次重启时间：</span>
            <span class="msg">{{$formatDate.dateFormat(viewDetailObj.zuulLastStartTime)}}</span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['monitorDetailParams'],
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
      this.viewDetailObj = {
        adminHotsName: this.monitorDetailParams.adminHotsName || '',
        adminInstanceid: this.monitorDetailParams.adminInstanceid || '',
        adminStatus: this.monitorDetailParams.adminStatus || '',
        zuulLastStartTime: this.monitorDetailParams.zuulLastStartTime || ''
      }
    },
    showHiddenMonitor: function () {
      this.$emit('showHiddenMonitor', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/gateway-status-detail.tmpl.less';
</style>
