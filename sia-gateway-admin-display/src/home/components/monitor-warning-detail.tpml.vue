<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>告警详情</span>
          <i class="close-icon" @click="showHiddenMonitorWarning"></i>
        </div>
        <div class="info scroll-bar">
          <p>
            <span>实例ID：</span>
            <span class="msg">{{viewDetailObj.id}}</span>
          </p>
          <p>
            <span>实例名称：</span>
            <span class="msg">{{viewDetailObj.zuulInstance}}</span>
          </p>
          <p>
            <span>实例创建时间：</span>
            <span class="msg">{{$formatDate.dateFormat(viewDetailObj.alarmCreateTime)}}</span>
          </p>
          <p>
            <span>描述：</span>
            <span class="msg">{{viewDetailObj.alarmInfomation}}</span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['monitorWarningParams'],
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
        id: this.monitorWarningParams.id || '',
        zuulInstance: this.monitorWarningParams.zuulInstance || '',
        alarmCreateTime: this.monitorWarningParams.alarmCreateTime || '',
        alarmInfomation: this.monitorWarningParams.alarmInfomation || ''
      }
    },
    showHiddenMonitorWarning: function () {
      this.$emit('showHiddenMonitorWarning', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/gateway-status-detail.tmpl.less';
</style>
