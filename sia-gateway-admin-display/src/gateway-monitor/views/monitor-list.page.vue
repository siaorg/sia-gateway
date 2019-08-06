<template>
    <div class="dispatch-system-default gateway-monitor-page">
      <div class="section-container monitor">
      </div>
    </div>
</template>

<script>
export default {
  name: 'GatewayMonitorPage',
  mounted () {
    this.getTaskList()
  },
  methods: {
    getTaskList: function () {
      let self = this
      let domEle = document.getElementsByClassName('section-container')[0]
      self.$http.get(self.$api.getApiAddress('/getMonitorUrl', 'CESHI_API_HOST'), {
        hostInfo: this.$route.query.ipport
      }).then((res) => {
        if (res.data.code === 200) {
          domEle.innerHTML = `<iframe src="${res.data.response}" width='100%' height='100%'></iframe>`
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    showHiddenJvmDetails: function () {
      this.$emit('showHiddenJvmDetails', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/gateway-monitor.page.less";
</style>
<style lang="less">
@import "../styles/gateway-monitor.page.reset.less";
</style>
