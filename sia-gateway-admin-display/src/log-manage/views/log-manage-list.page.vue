<template>
    <div class="dispatch-system-default log-manage-list-page">
      <div class="section-container">
      </div>
    </div>
</template>

<script>
export default {
  name: 'LogManageListPage',
  data () {
    return {
    }
  },
  mounted () {
    this.initLogPage()
  },
  methods: {
    initLogPage: function () {
      let routerId = this.$route.query.id
      let ipAddress = this.$api.getApiAddress('/', 'CESHI_API_HOST_LOG')
      if (routerId !== '' && routerId !== undefined) {
        this.getLogList(routerId)
      } else {
        this.getLogList(this.$store.state.frame.currentGatewayGroup)
      }
    },
    getRouterLogList: function (index) {
      let ipAddress = this.$api.getApiAddress('/', 'CESHI_API_HOST_LOG')
      let domEle = document.getElementsByClassName('section-container')[0]
      domEle.innerHTML = `<iframe src="${ipAddress}app/kibana#/discover?_g=()&_a=(columns:!(_source),index:'${index}',interval:auto,query:(match_all:()),sort:!(logtime,desc))" width='100%' height='100%'></iframe>`
    },
    getLogList: function (val) {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getLogIndexMap', 'CESHI_API_HOST'), {
        indexId: val
      }).then((res) => {
        if (res.data.code === 200) {
          self.getRouterLogList(res.data.response)
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/log-manage-list.page.less";
</style>
<style lang="less">
@import "../styles/log-manage-list.page.reset.less";
</style>
