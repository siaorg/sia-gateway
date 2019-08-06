<template>
  <div class="dispatch-system-default run-static-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">{{$route.query.id}} ~ 运行统计详情</span>
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="back-btn" @click="handleClickReturn"><img src="../images/back-icon.png" alt="" class="img">返回 </el-button>
      </div>
      <div class="section-content">
        <el-table :data="runCountList" border style="width: 100%" class="task-manage-table">
          <el-table-column prop="groupName" label="网关集群组名" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="routeid" label="路由ID" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="url" label="URL" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="sumCount" label="sumCount" width="130" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="failedCount" label="failedCount" width="130" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="maxSpan" label="maxSpan(ms)" align="center" width="130" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="minSpan" label="minSpan(ms)" align="center" width="130" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="avgSpan" label="avgSpan(ms)" align="center" width="130" show-overflow-tooltip>
            <template slot-scope="scope">
              <div class="box-color">
                <span>{{scope.row.avgSpan}} ({{((scope.row.avgSpan/scope.row.maxSpan)*100).toFixed(2)}})</span>
                <span><i :style="{background: colorTag(scope.row), width: ((scope.row.avgSpan/scope.row.maxSpan)*100).toFixed(4) + '%'}"></i></span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RunStaticDetailPage',
  data () {
    return {
      loadingRefresh: false,
      runCountList:  [ 
        // { 
        //   "url": "/op/test/pppppp", 
        //   "routeid": "mutiRegister", 
        //   "instanceId": null, 
        //   "groupName": "DEV-GATEWAY-CORE", 
        //   "lastInvokeTime": 1562148271523, 
        //   "sumCount": 2, 
        //   "failedCount": 0, 
        //   "sumSpan": 24691, 
        //   "maxSpan": 12593, 
        //   "minSpan": 12098, 
        //   "avgSpan": 12345 
        // }
      ]
    }
  },
  mounted () {
    this.getHealthMonitorData()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    colorTag: function (val) {
      if (val.avgSpan <= 1000) {
        return '#15D8E3'
      } else if  (1000 < val.avgSpan <= 2000) {
        return '#FFEB3B'
      } else if  (2000 < val.avgSpan <= 4000) {
        return '#E8A010'
      } else if (4000 < val.avgSpan) {
        return '#E91E63'
      }
    },
    getHealthMonitorData: function () {
      let self = this
      let paramsModal = {
        groupName: this.$route.query.zuulGroupName
      }
      self.$http.post(self.$api.getApiAddress('/getURLRecord', 'CESHI_API_HOST'), paramsModal).then((res) => {
        if (res.data.code === 200) {
          self.runCountList = res.data.response
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickReturn: function () {
      this.clearAction('return')
    },
    clearAction: function (operate) {
      let self = this
      if (operate === 'return') {
        self.$router.push({path: '/route-manage-list'})
        // 添加分页时需要传参
        // self.$router.push({ path: '/route-manage-list', query: { currentPageIndex: self.$route.query.currentPageIndex } })
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/run-static-detail.page.less";
</style>
