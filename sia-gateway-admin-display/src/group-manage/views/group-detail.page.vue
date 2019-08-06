<template>
  <div class="dispatch-system-default route-create-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">{{$route.query.name}} ~ 组件详情</span>
        <!--<el-button class="button blue-button" @click="handleClickReturn"> 返回 </el-button>-->
        <el-button class="back-btn btn-ml-auto" @click="handleClickReturn"><img src="../images/back-icon.png" alt="" class="img">返回 </el-button>
      </div>
      <div class="section-content">
        <el-form class="routeCreateViewForm">
          <el-form-item label="组件名称：">
            <span>{{groupDetailViewModel.compName}}</span>
          </el-form-item>
          <el-form-item label="执行位置：">
            <span>{{groupDetailViewModel.compType | filterCmpType}}</span>
          </el-form-item>
          <el-form-item label="优先级：">
            <span>{{groupDetailViewModel.compOrder}}</span>
          </el-form-item>
          <el-form-item label="状态：">
            <span>{{groupDetailViewModel.status | filterStatus}}</span>
          </el-form-item>
          <el-form-item label="更新时间：">
            {{$formatDate.dateFormat(groupDetailViewModel.compUpdateTime)}}
          </el-form-item>
          <el-form-item label="绑定的路由：">
            <p class="router-list" v-if="groupDetailViewModel.context!=='' && groupDetailViewModel.context !== null">
              <span v-for="(item,index) in groupDetailViewModel.context" :key="index" :label="item" v-show="item !== ''"><i>{{item}}</i></span>
            </p>
          </el-form-item>
          <el-form-item label="描述：">
            <span>{{groupDetailViewModel.compdesc}}</span>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GroupDetailPage',
  data () {
    return {
      groupDetailViewModel: {
        compName: '',
        compType: '',
        compOrder: '',
        status: '',
        compUpdateTime: '',
        context: '',
        compdesc: ''
      }
    }
  },
  filters: {
    filterStatus (val) {
      switch (val) {
        case 'ok':
          return '发布'
      }
    },
    filterCmpType (val) {
      switch (val) {
        case 'PRE':
          return '调用前'
        case 'POST':
          return '调用后'
        case 'ROUTE':
          return '调用中'
      }
    }
  },
  created () {
    this.getGroupDetail()
  },
  methods: {
    initData: function (val) {
      this.groupDetailViewModel = {
        compName: val.compName,
        compType: val.compType,
        compOrder: val.compOrder,
        status: val.status,
        compUpdateTime: val.compUpdateTime,
        context: val.context,
        compdesc: val.compdesc
      }
    },
    getGroupDetail: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getCompDetail', 'CESHI_API_HOST'), {
        compFilterName: this.$route.query.id
      }).then((res) => {
        self.initData(res.data.response)
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
        self.$router.push({path: '/group-manage-list'})
        // 添加分页时需要传参
        // self.$router.push({ path: '/group-manage-list', query: { currentPageIndex: self.$route.query.currentPageIndex } })
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/group-detail.page.less";
</style>
<style lang="less">
@import "../styles/group-detail.page.reset.less";
</style>
