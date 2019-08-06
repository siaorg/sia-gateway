<template>
    <div class="dispatch-system-default fuse-manage-page">
      <div class="section-container">
        <div class="section-header">
          <div class="search-box">
            <div class="search-list">
              <span>Search Key</span>
              <el-input type="text" auto-complete="off" placeholder="请输入Search Key" v-model="searchKeyword"></el-input>
            </div>
            <div class="search-list">
              <span>开始日期</span>
              <el-date-picker
                v-model="hourTimeValue"
                type="date"
                placeholder="开始日期">
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
          <el-table :data="viewAuditList" border style="width: 100%" class="fuse-manage-table">
            <el-table-column prop="zuulInstance" label="网关实例" show-overflow-tooltip width="220">
            </el-table-column>
            <el-table-column prop="fallbackType" label="熔断类型" show-overflow-tooltip width="160">
            </el-table-column>
            <el-table-column prop="zuulGroupName" label="网关集群组名" show-overflow-tooltip width="220">
            </el-table-column>
            <el-table-column prop="startTime" label="时间" align="center" width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                {{$formatDate.dateFormat(scope.row.startTime)}}
              </template>
            </el-table-column>
            <el-table-column prop="fallbackMsg" label="返回错误信息" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="stackTrace" label="stackTrace">
              <template slot-scope="scope">
                <div class="show-info">
                  <span class="">{{scope.row.stackTrace}}</span>
                  <span @click="showStackTraceInfo(scope.row.stackTrace)">详情</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination background v-show="pageCount!=0" layout="sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 30, 40]" :page-count="pageCount" :current-page="currentPageIndex" @size-change="handleSizeChange" :page-size="pageSize" @current-change="handleCurrentChange">
          </el-pagination>
        </div>
      </div>
      <trace-info-tmpl  v-if="traceInfoShow" :traceInfoParams="traceInfoParams" v-on:showHiddenTraceInfo="showHiddenTraceInfo">
      </trace-info-tmpl>
    </div>
</template>

<script>
const traceInfoTmpl = resolve => require(['../components/trace-info.tmpl'], resolve)
export default {
  name: 'RouteManageListPage',
  components: {traceInfoTmpl},
  data () {
    return {
      pageCount: 0,
      currentPageIndex: 1,
      pageSize: 10,
      traceInfoShow: false,
      traceInfoParams: '',
      searchKeyword: '',
      hourTimeValue: '',
      viewAuditList:  [ 
        {
          'zuulGroupName': 'DEV-GATEWAY-CORE', 
          'instanceId': null, 
          'zuulInstance': '10.10.168.19:8080', 
          'fallbackMsg': 'Load balancer does not have available server for client: hello-book', 
          'stackTrace': 'org.springframework.cloud.netflix.ribbon.support.AbstractLoadBalancingClient.validateServiceInstance(AbstractLoadBalancingClient.java:173)org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient$1.doWithRetry(RetryableRibbonLoadBalancingHttpClient.java:128)org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient$1.doWithRetry(RetryableRibbonLoadBalancingHttpClient.java:120)org.springframework.retry.support.RetryTemplate.doExecute(RetryTemplate.java:287)org.springframework.retry.support.RetryTemplate.execute(RetryTemplate.java:180)org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.executeWithRetry(RetryableRibbonLoadBalancingHttpClient.java:178)org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.execute(RetryableRibbonLoadBalancingHttpClient.java:152)org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.execute(RetryableRibbonLoadBalancingHttpClient.java:60)org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand.run(AbstractRibbonCommand.java:185)org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand.run(AbstractRibbonCommand.java:52)com.netflix.hystrix.HystrixCommand$2.call(HystrixCommand.java:302)com.netflix.hystrix.HystrixCommand$2.call(HystrixCommand.java:298)rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:46)rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:35)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48)rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30)rx.Observable.unsafeSubscribe(Observable.java:10151)rx.inter', 
          'startTime': 1560857410000 
        }
      ]
    }
  },
  created () {
    this.getGatewayAuditList()
  },
  methods: {
    showStackTraceInfo: function (val) {
      this.traceInfoShow = true
      this.traceInfoParams = val
    },
    showHiddenTraceInfo: function (val) {
      this.traceInfoShow = val
    },
    getGatewayAuditList: function () {
      let self = this
      let date = this.hourTimeValue !== '' ? this.$formatDate.dateFormat(new Date(this.hourTimeValue).getTime()) : ''
      let params = {
        'pageNo': this.currentPageIndex,
        'pageSize': this.pageSize,
        'sortName': 'startTime',
        'sortType': 'desc',
        'searchKeyword': this.searchKeyword, 
	      'startTime': date !== '' ? date.split(' ')[0] : ''
      }
      self.$http.post(self.$api.getApiAddress('/getFallback', 'CESHI_API_HOST'), params).then((res) => {
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
@import "../styles/fuse-manage.page.less";
</style>
