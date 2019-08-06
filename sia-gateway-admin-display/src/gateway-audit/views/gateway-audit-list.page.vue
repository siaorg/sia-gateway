<template>
    <div class="dispatch-system-default gateway-audit-list-page">
      <div class="section-container">
        <div class="section-header">
          <div class="search-box">
            <div class="search-list">
              <span>邮箱（前缀）</span>
              <el-input type="text" auto-complete="off" placeholder="请输入邮箱" v-model="emailValue"></el-input>
            </div>
            <div class="search-list">
              <span>发生日期</span>
              <el-date-picker
                class="data-picker"
                v-model="hourTimeValue"
                type="datetimerange"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="['00:00:00', '23:59:59']">
              </el-date-picker>
            </div>
            <div class="search-list">
              <el-button class="btn search-btn" @click="handleClickSearchRoute"> 查询 </el-button>
              <el-button class="btn reset-btn" @click="handleClickResetSearchInfo"> 重置 </el-button>
            </div>
          </div>
        </div>
        <div class="bg-empty"></div>
        <div class="section-content">
          <el-table :data="viewAuditList" border style="width: 100%" class="task-manage-table">
            <el-table-column prop="userName" label="用户名" width="120" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="zuulGroupName" label="网关集群组名" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="method" label="请求方式" width="75" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="url" label="请求URL" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="ip" label="调用方IP" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="timeLoss" label="时间损耗（毫秒）" width="130" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="status" label="执行结果" align="center" width="95" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.status === 1 ? '成功' : '失败'}}
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="发生时间" align="center" width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                {{$formatDate.dateFormat(scope.row.startTime)}}
              </template>
            </el-table-column>
            <el-table-column prop="params" label="请求参数" align="center" show-overflow-tooltip>
            </el-table-column>
          </el-table>
          <el-pagination background v-show="pageCount!=0" layout="sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 30, 40]" :page-count="pageCount" :current-page="currentPageIndex" @size-change="handleSizeChange" :page-size="pageSize" @current-change="handleCurrentChange">
          </el-pagination>
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'gatewayAuditListPage',
  data () {
    return {
      pageCount: 0,
      currentPageIndex: 1,
      pageSize: 10,
      emailValue: '',
      hourTimeValue: ['', ''],
      viewAuditList: []
    }
  },
  watch: {
    'hourTimeValue': function (newValue) {
      console.log(newValue, '-----------------------new')
    }
  },
  created () {
    this.getGatewayAuditList()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getGatewayAuditList()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    getGatewayAuditList: function () {
      let self = this
      let params = {
        'pageNo': this.currentPageIndex,
        'pageSize': this.pageSize,
        'sortName': 'startTime',
        'sortType': 'desc',
        'startTime': this.hourTimeValue[0] !== '' ? this.hourTimeValue[0] : '',
        'endTime': this.hourTimeValue[1] !== '' ? this.hourTimeValue[1] : '',
        'userName': this.emailValue
      }
      self.$http.post(self.$api.getApiAddress('/queryAuditInfos', 'CESHI_API_HOST'), params).then((res) => {
        if (res.data.code === 200) {
          self.viewAuditList = res.data.response.dataList
          self.pageCount = res.data.response.totalPage
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickResetSearchInfo: function () {
      this.currentPageIndex = 1
      this.hourTimeValue = ['', '']
      this.emailValue = ''
      this.getGatewayAuditList()
    },
    handleClickSearchRoute: function () {
      this.getGatewayAuditList()
    },
    // 页数  变化
    handleCurrentChange: function (val) {
      this.currentPageIndex = val
      this.getGatewayAuditList()
    },
    // 一页显示条数  变化
    handleSizeChange: function (val) {
      this.pageSize = val
      this.currentPageIndex = 1
      this.getGatewayAuditList()
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/gateway-audit-list.page.less";
</style>
