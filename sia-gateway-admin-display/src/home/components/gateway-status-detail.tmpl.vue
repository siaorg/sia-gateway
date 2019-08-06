<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>网关状态详情</span>
          <i class="close-icon" @click="showHiddenGateway"></i>
        </div>
        <div class="info more">
          <p>
            <span>主机名称：</span>
            <span class="msg">{{viewDetailObj.zuulHotsName}}</span>
          </p>
          <p>
            <span>实例ID：</span>
            <span class="msg">{{viewDetailObj.zuulInstanceId}}</span>
          </p>
          <p>
            <span>网管集群名：</span>
            <span class="msg">{{viewDetailObj.zuulGroupName}}</span>
          </p>
          <p>
            <span>状态：</span>
            <span class="msg">{{viewDetailObj.zuulStatus}}</span>
          </p>
          <p>
            <span>最后一次重启时间：</span>
            <span class="msg">{{$formatDate.dateFormat(viewDetailObj.zuulLastStartTime)}}</span>
          </p>
          <p>
            <span>描述：</span>
            <span class="msg">{{viewDetailObj.zuulDesc}}</span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['gatewayDetailParams'],
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
      // let self = this
      // self.$http.post(self.$api.getApiAddress('/queryZuul', 'CESHI_API_HOST'), {
      //   zuulInstanceId: this.gatewayDetailParams
      // }).then((res) => {
      //   self.viewDetailObj = res.data
      // }).catch(() => {
      //   self.$message({message: '服务未响应！', type: 'error'})
      // })
      this.viewDetailObj = {
        zuulHotsName: this.gatewayDetailParams.zuulHotsName || '',
        zuulInstanceId: this.gatewayDetailParams.zuulInstanceId || '',
        zuulGroupName: this.gatewayDetailParams.zuulGroupName || '',
        zuulStatus: this.gatewayDetailParams.zuulStatus || '',
        zuulLastStartTime: this.gatewayDetailParams.zuulLastStartTime || '',
        zuulDesc: this.gatewayDetailParams.zuulDesc || ''
      }
    },
    showHiddenGateway: function () {
      this.$emit('showHiddenGateway', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/gateway-status-detail.tmpl.less';
</style>
