<template>
  <div class="dispatch-system-default route-create-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">{{$route.query.id}} ~ 修改</span>
        <i class="el-icon-question instruction-icon" @click="handleClickShowInstruction"></i>
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="btn" @click="handleClickReturn"> 返回 </el-button>
      </div>
      <div class="section-content">
        <el-form :model="routeCreateViewModel" :rules="routeCreateViewModelRules" ref="routeCreateViewForm" class="routeCreateViewForm">
          <el-form-item label="路由名称：" prop="routeName">
            <el-input type="text" v-model="routeCreateViewModel.routeName" :disabled="true" auto-complete="off" placeholder="请输入路由名称"></el-input>
          </el-form-item>
          <el-form-item label="网关集群组名：" prop="gatewayClusterTag">
            <el-select placeholder="请选择网关集群组名" v-model="routeCreateViewModel.gatewayClusterTag" v-show="!gatewayIsSelect">
              <el-option v-for="(item,index) in gatewayClusterList" :key="index" :label="item" :value="item"></el-option>
            </el-select>
            <el-input type="text" v-model="routeCreateViewModel.gatewayClusterTag" auto-complete="off" placeholder="请输入网关集群组名" v-show="gatewayIsSelect"></el-input>
          </el-form-item>
          <el-form-item label="应用名称：" prop="applyName">
            <el-input type="text" v-model="routeCreateViewModel.applyName" auto-complete="off" placeholder="请输入应用名称"></el-input>
          </el-form-item>
          <el-form-item label="匹配路径：" prop="configPath">
            <el-input type="text" v-model="routeCreateViewModel.configPath" auto-complete="off" placeholder="请输入匹配路径"></el-input>
          </el-form-item>
          <el-form-item label="后端服务策略：" prop="serverCase" class="radio-text">
            <el-radio-group v-model="routeCreateViewModel.serverCase">
              <el-radio label="SERVICEID">后端服务ID</el-radio>
              <el-radio label="SERVICEURL">后端服务URL</el-radio>
              <el-radio label="LISTOFSERVER">ListofServer</el-radio>
            </el-radio-group>
          </el-form-item>
          <!--<el-form-item :label="routeCreateViewModel.serverCase === 'SERVICEID' ? '后端服务ID：' : (routeCreateViewModel.serverCase === 'SERVICEURL'?'后端服务URL': '后端服务')" prop="serverCaseTag">
            <el-input v-if="routeCreateViewModel.serverCase === 'SERVICEID'" :disabled="routeCreateViewModel.serverCase===''" type="text" v-model="routeCreateViewModel.serverCaseTag" auto-complete="off" :placeholder="routeCreateViewModel.serverCase===''?'请先选择后端服务策略' : '请输入后端服务ID'"></el-input>
            <el-input v-else type="text" :disabled="routeCreateViewModel.serverCase===''" v-model="routeCreateViewModel.serverCaseTag" auto-complete="off" :placeholder="routeCreateViewModel.serverCase===''?'请先选择后端服务策略' : '请输入后端服务URL'"></el-input>
          </el-form-item>-->
          <el-form-item label="后端服务：" v-show="routeCreateViewModel.serverCase===''">
            <el-input :disabled="true" type="text"placeholder="请先选择后端服务策略"></el-input>
          </el-form-item>
          <el-form-item label="后端服务ID：" prop="serverCaseId" v-if="isShowServerId">
            <el-input :disabled="routeCreateViewModel.serverCase===''" type="text" v-model="routeCreateViewModel.serverCaseId" auto-complete="off" placeholder="请输入后端服务ID"></el-input>
          </el-form-item>
          <el-form-item label="后端服务URL：" prop="serverCaseUrl" v-if="isShowServerUrl">
            <el-input :disabled="routeCreateViewModel.serverCase===''" type="text" v-model="routeCreateViewModel.serverCaseUrl" auto-complete="off" placeholder="请输入后端服务URL"></el-input>
          </el-form-item>
          <!--<el-form-item label="重试次数：" prop="retryCount">
            <el-radio-group v-model="routeCreateViewModel.retryCount">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>-->
          <el-form-item label="前缀是否生效：" prop="preFix">
            <el-radio-group v-model="routeCreateViewModel.preFix">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item class="save-btn-form">
            <el-button @click="handleClickSave"> 修改 </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <router-instruction-tmpl  v-if="routerInstructoinShow" v-on:showHiddenRouterInstructoin="showHiddenRouterInstructoin"></router-instruction-tmpl>
  </div>
</template>

<script>
const routerInstructionTmpl = resolve => require(['../components/router-instruction.tmpl.vue'], resolve)
export default {
  name: 'RouteEditPage',
  components: {routerInstructionTmpl},
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        // case 'retryCount':
        //   if (!new RegExp(/^[1-9][0-9]{0,}$/).test(value)) {
        //     return callback(new Error('重试次数必须为数字！'))
        //   }
        //   break
        case 'configPath':
          if (!new RegExp(/^(\/[(a-zA-Z)+(0-9_)?]+)+/i).test(value)) {
            return callback(new Error('匹配路径格式必须以 " / "开头!'))
          }
          if (!new RegExp(/(\/\*\*)$/i).test(value) && !new RegExp(/(\/\?)$/i).test(value) && !new RegExp(/(\/\*)$/i).test(value)) {
            return callback(new Error('匹配路径格式必须以 " /**、/?、/* "结尾'))
          }
          break
        case 'serverCaseTag':
          let regEn = /[`~!@#$%^&*()_+<>"{},;[\]]/g
          let regCn = /[！#￥（）；“”‘、，|《。》、【】[]]/g
          if (regEn.test(value) || regCn.test(value)) {
            return callback(new Error('后端服务不能包含特殊字符!'))
          }
          break
      }
      callback()
    }
    return {
      loadingRefresh: false,
      routerInstructoinShow: false,
      gatewayIsSelect: true,
      routeCreateViewModel: {
        routeName: '',
        gatewayClusterTag: '',
        applyName: '',
        configPath: '',
        serverCase: '',
        serverCaseId: '',
        serverCaseUrl: '',
        // retryCount: '',
        preFix: ''
      },
      initRouteStatus: '',
      gatewayClusterList: [],
      routeCreateViewModelRules: {
        routeName: [this.$validator.required('请输入路由名称'), { validator: checkBlur, trigger: 'blur' }],
        gatewayClusterTag: [this.$validator.required('请选择网关集群组名'), { validator: checkBlur, trigger: 'blur' }],
        configPath: [this.$validator.required('请输入匹配路径'), { validator: checkBlur, trigger: 'blur' }],
        serverCase: [this.$validator.required('请选择后端服务策略'), { validator: checkBlur, trigger: 'blur' }],
        serverCaseUrl: [this.$validator.required('请输入后端服务Url'), { validator: checkBlur, trigger: 'blur' }],
        serverCaseId: [this.$validator.required('请输入后端服务Id'), { validator: checkBlur, trigger: 'blur' }]
      },
      isShowServerId: false,
      isShowServerUrl: false
    }
  },
  watch: {
    'routeCreateViewModel.serverCase': function (newValue) {
      this.isShowServerId = (newValue === 'SERVICEID' || newValue === 'LISTOFSERVER') ? true : false
      this.isShowServerUrl = (newValue === 'SERVICEURL' || newValue === 'LISTOFSERVER') ? true : false
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCaseId')
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCaseUrl')
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCase')
    }
  },
  created () {
    this.getRouteInfo()
  },
  mounted () {
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getRouteInfo()
      this.$refs.routeCreateViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    getZuulGroupList: function () {
      let self = this
      self.gatewayClusterList = JSON.parse(sessionStorage.getItem('selectAuth'))
      if (self.gatewayClusterList == null || JSON.parse(sessionStorage.getItem('selectAuth')).indexOf('admin') === -1) {
        self.gatewayIsSelect = false
      } else {
        self.gatewayIsSelect = true
      }
    },
    handleClickShowInstruction: function () {
      this.routerInstructoinShow = true
    },
    showHiddenRouterInstructoin: function (val) {
      this.routerInstructoinShow = val
    },
    initData: function (val) {
      this.routeCreateViewModel = {
        routeName: this.$route.query.id,
        gatewayClusterTag: val.zuulGroupName,
        applyName: val.apiName,
        configPath: val.path,
        serverCase: val.strategy,
        serverCaseId: val.serviceId === 'null' ? '' : val.serviceId,
        serverCaseUrl: val.url === 'null' ? '' : val.url,
        // retryCount: val.retryable,
        preFix: val.stripPrefix
      }
      this.initRouteStatus = val.routeStatus
      this.$refs.routeCreateViewForm.clearValidate()
    },
    getRouteInfo: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getRouteByRouteID', 'CESHI_API_HOST'), {
        routeid: this.$route.query.id
      }).then((res) => {
        this.getZuulGroupList()
        self.initData(res.data.response)
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickReturn: function () {
      let self = this
      self.$confirm('你确定要返回路由列表么?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        self.clearAction('return')
      })
    },
    handleClickSave: function () {
      let self = this
      let paramsObj = {
        'routeid': this.$route.query.id,
        'path': self.routeCreateViewModel.configPath,
        'strategy': self.routeCreateViewModel.serverCase,
        'serviceId': (self.routeCreateViewModel.serverCase === 'SERVICEID' || self.routeCreateViewModel.serverCase === 'LISTOFSERVER') ? self.routeCreateViewModel.serverCaseId : '',
        'url': (self.routeCreateViewModel.serverCase === 'SERVICEURL' || self.routeCreateViewModel.serverCase === 'LISTOFSERVER') ? self.routeCreateViewModel.serverCaseUrl : '',
        // 'retryable': self.routeCreateViewModel.retryCount,
        'stripPrefix': self.routeCreateViewModel.preFix,
        'apiName': self.routeCreateViewModel.applyName,
        'zuulGroupName': self.routeCreateViewModel.gatewayClusterTag,
        'enabled': true,
        'routeStatus': this.initRouteStatus
      }
      this.$refs.routeCreateViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('/updateRoute', 'CESHI_API_HOST'), paramsObj).then(res => {
            if (res.data.code === 200) {
              self.$message({ message: '修改成功', type: 'success' })
              self.clearAction('return')
            } else {
              self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
            }
          }).catch((err) => {
            self.$message({ message: this.$helper.handleLoginErrorMsg(err), type: 'error' })
          })
        }
      })
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
@import "../styles/route-create.page.less";
</style>
<style lang="less">
@import "../styles/route-create.page.reset.less";
</style>
