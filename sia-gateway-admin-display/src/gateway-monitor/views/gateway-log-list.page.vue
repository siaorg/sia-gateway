<template>
    <div class="dispatch-system-default gateway-log-page">
      <div class="section-container scroll-bar">
      </div>
    </div>
</template>

<script>
export default {
  name: 'GatewayLogPage',
  mounted () {
    this.getGantewayLog()
  },
  methods: {
    getGantewayLog: function () {
      let self = this
      let domEle = document.getElementsByClassName('section-container')[0]
      // domEle.innerHTML = `<iframe src=http://10.143.135.138:8040/monitor/logfile?ipport=10.251.77.7:8080 width='100%' height='100%'></iframe>`
      self.$http.get(self.$api.getApiAddress('/monitor/getLogUrl', 'CESHI_API_HOST'), {
        ipport: this.$route.query.ipport
      }).then((res) => {
        // console.log(res.data.response, '---------------res.data.response')
        if (res.data.code === 200) {
          domEle.innerHTML = `<iframe src="${res.data.response}" width='100%' height='100%'></iframe>`
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/gateway-log-list.page.less";
</style>
<style lang="less">
@import "../styles/gateway-monitor.page.reset.less";
</style>
