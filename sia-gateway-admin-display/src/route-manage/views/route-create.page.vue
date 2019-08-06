<template>
  <div class="dispatch-system-default route-create-page">
    <div class="section-container">
      <div class="section-header">
        <!--<div class="header-no-list">-->
          <span class="title">新建路由</span>
          <i class="el-icon-question instruction-icon" @click="handleClickShowInstruction"></i>
          <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
          <!--<el-button class="btn" @click="handleClickReturn"> 返回 </el-button>-->
          <el-button class="back-btn" @click="handleClickReturn"><img src="../images/back-icon.png" alt="" class="img">返回 </el-button>
        <!--</div>-->
      </div>
      <div class="section-content">
        <el-form :model="routeCreateViewModel" :rules="routeCreateViewModelRules" ref="routeCreateViewForm" class="routeCreateViewForm">
          <el-form-item label="路由ID：" prop="routeId">
            <el-input type="text" v-model="routeCreateViewModel.routeId" auto-complete="off" placeholder="请输入路由名称"></el-input>
          </el-form-item>
          <div>
            <el-col>
              <el-form-item label="网关集群组名：" prop="gatewayClusterTag">
                <el-select placeholder="请选择网关集群组名" v-model="routeCreateViewModel.gatewayClusterTag" v-show="!gatewayIsSelect">
                  <el-option v-for="(item,index) in gatewayClusterList" :key="index" :label="item" :value="item"></el-option>
                </el-select>
                <el-input type="text" v-model="routeCreateViewModel.gatewayClusterTag" auto-complete="off" placeholder="请输入网关集群组名" v-show="gatewayIsSelect"></el-input>
              </el-form-item>
            </el-col>
            <div style="clear:both;"></div>
          </div>
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
          <!--<el-form-item :label="routeCreateViewModel.serverCase === 'SERVICEID' ? '后端服务ID：' : (routeCreateViewModel.serverCase === 'SERVICEURL'?'后端服务URL：': '后端服务:')" prop="serverCaseUrl">
            <el-input v-if="routeCreateViewModel.serverCase === 'SERVICEID'" :disabled="routeCreateViewModel.serverCase===''" type="text" v-model="routeCreateViewModel.serverCaseUrl" auto-complete="off" :placeholder="routeCreateViewModel.serverCase===''?'请先选择后端服务策略' : '请输入后端服务ID'"></el-input>
            <el-input v-else type="text" :disabled="routeCreateViewModel.serverCase===''" v-model="routeCreateViewModel.serverCaseUrl" auto-complete="off" :placeholder="routeCreateViewModel.serverCase===''?'请先选择后端服务策略' : '请输入后端服务URL'"></el-input>
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
          <!--<el-form-item label="是否重试：" prop="retryCount">
            <el-radio-group v-model="routeCreateViewModel.retryCount">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>-->
          <el-form-item label="前缀是否生效：" prop="preFix">
            <!--<el-input type="text" v-model="routeCreateViewModel.preFix" auto-complete="off" placeholder="请输入前缀信息"></el-input>-->
            <el-radio-group v-model="routeCreateViewModel.preFix">
              <el-radio :label="true">是</el-radio>
              <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item class="save-btn-form">
            <!--<el-button @click="handleClickCancel"> 取消 </el-button>-->
            <el-button @click="handleClickSave"> 创建 </el-button>
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
  name: 'RouteCreatePage',
  components: {routerInstructionTmpl},
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'configPath':
          if (!new RegExp(/^(\/[(a-zA-Z)+(0-9_)?]+)+/i).test(value)) {
            return callback(new Error('匹配路径格式必须以 " / "开头!'))
          }
          if (!new RegExp(/(\/\*\*)$/i).test(value) && !new RegExp(/(\/\?)$/i).test(value) && !new RegExp(/(\/\*)$/i).test(value)) {
            return callback(new Error('匹配路径格式必须以 " /**、/?、/* "结尾'))
          }
          break
        case 'serverCaseUrl':
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
        routeId: '',
        gatewayClusterTag: '',
        applyName: '',
        configPath: '',
        serverCase: '',
        serverCaseId: '',
        serverCaseUrl: '',
        // retryCount: false,
        preFix: false
      },
      gatewayClusterList: [],
      routeCreateViewModelRules: {
        routeId: [this.$validator.required('请输入路由名称'), { validator: checkBlur, trigger: 'blur' }],
        gatewayClusterTag: [this.$validator.required('网关集群组名不能为空'), { validator: checkBlur, trigger: 'blur' }],
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
    'routeCreateViewModel.gatewayClusterTag': function () {
      this.$refs.routeCreateViewForm.validate('routeCreateViewModel.gatewayClusterTag')
    },
    'routeCreateViewModel.serverCase': function (newValue) {
      this.isShowServerId = (newValue === 'SERVICEID' || newValue === 'LISTOFSERVER') ? true : false
      this.isShowServerUrl = (newValue === 'SERVICEURL' || newValue === 'LISTOFSERVER') ? true : false
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCaseId')
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCaseUrl')
      // this.$refs.routeCreateViewForm.validate('routeCreateViewModel.serverCase')
    }
  },
  created () {
    this.getZuulGroupList()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getZuulGroupList()
      this.handleClickCancel()
      this.$refs.routeCreateViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    getZuulGroupList: function () {
      let self = this
      self.gatewayClusterList = JSON.parse(sessionStorage.getItem('selectAuth'))
      if (JSON.parse(sessionStorage.getItem('selectAuth')).indexOf('admin') === -1) {
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
        'routeid': self.routeCreateViewModel.routeId,
        'path': self.routeCreateViewModel.configPath,
        'strategy': self.routeCreateViewModel.serverCase,
        'serviceId': (self.routeCreateViewModel.serverCase === 'SERVICEID' || self.routeCreateViewModel.serverCase === 'LISTOFSERVER') ? self.routeCreateViewModel.serverCaseId : '',
        'url': (self.routeCreateViewModel.serverCase === 'SERVICEURL' || self.routeCreateViewModel.serverCase === 'LISTOFSERVER') ? self.routeCreateViewModel.serverCaseUrl : '',
        // 'retryable': self.routeCreateViewModel.retryCount,
        'stripPrefix': self.routeCreateViewModel.preFix,
        'apiName': self.routeCreateViewModel.applyName,
        'zuulGroupName': self.routeCreateViewModel.gatewayClusterTag,
        'enabled': true
      }
      this.$refs.routeCreateViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('/addRoute', 'CESHI_API_HOST'), paramsObj).then(res => {
            if (res.data.code === 200) {
              self.$message({ message: '新建成功', type: 'success' })
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
    handleClickCancel: function () {
      let self = this
      self.routeCreateViewModel = {
        routeId: '',
        gatewayClusterTag: '',
        applyName: '',
        configPath: '',
        serverCase: '',
        serverCaseId: '',
        serverCaseUrl: '',
        // retryCount: true,
        preFix: true
      }
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
