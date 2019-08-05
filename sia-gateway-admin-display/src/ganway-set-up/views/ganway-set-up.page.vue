<template>
  <div class="dispatch-system-default black-white-detail-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">网关设置</span>
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
      </div>
      <div class="section-content">
        <el-form :model="ganwaySetUpViewModel" :rules="ganwaySetUpViewModelRules" ref="ganwaySetUpViewForm" class="ganwaySetUpViewForm">
          <h3>
            <span>预警邮箱</span>
          </h3>
          <el-form-item label="预警邮箱：" prop="warnningEmail">
            <el-input :disabled="showEditRouteStrategy" type="text" v-model.trim="ganwaySetUpViewModel.warnningEmail" auto-complete="off" placeholder="请输入预警邮箱"></el-input>
            <el-tooltip  v-if="!showEditRouteStrategy" class="item" effect="dark" content="多个邮箱以分号隔开!" placement="top">
              <span class="el-icon-question instruction-icon"></span>
            </el-tooltip>
          </el-form-item>
          <div class="right-btn-two">
            <el-button class="btn reset-btn" v-show="!showEditRouteStrategy" @click="handleClickCancel"> 取消 </el-button>
            <el-button class="delete-btn" @click="handleClickDelete" v-show="showEditRouteStrategy"> 删除 </el-button>
            <el-button class="btn search-btn" @click="handleClickEmailSave"> {{showEditRouteStrategy?'编辑':'保存'}}</el-button>
          </div>
        </el-form>
        <el-form :model="ganwaySetUpLog" :rules="ganwaySetUpLogRules" ref="ganwaySetUpLogViewForm" class="ganwaySetUpViewForm">
          <h3>
            <span>日志级别操作</span>
          </h3>
          <el-form-item label="操作类型：" prop="type" class="radio-text">
            <el-radio-group v-model="ganwaySetUpLog.type">
                <el-radio label="look">查看</el-radio>
                <el-radio label="set">设置</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="实例信息：" prop="ipport">
            <el-select placeholder="请选择实例信息" v-model="ganwaySetUpLog.ipport">
              <el-option v-for="(item,index) in viewGatewayStatusList" :key="index" :label="item.zuulInstanceId" :value="item.zuulInstanceId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" prop="nameLevel">
            <el-select placeholder="请选择LoggerName" v-model.trim="ganwaySetUpLog.name" class="ml">
              <el-option v-for="(item,index) in pathNameList" :key="index" :label="item" :value="item"></el-option>
            </el-select>
            {{ganwaySetUpLog.type === 'look' ? '' : '－'}}
            <el-select placeholder="请选择Level" v-model.trim="ganwaySetUpLog.level" class="ml" v-if="ganwaySetUpLog.type !== 'look'">
              <el-option v-for="(item,index) in pathLevelList" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日志级别：" v-show="logLevelTag !== ''">
            <el-input :disabled="true" type="text" v-model.trim="logLevelTag" auto-complete="off"></el-input>
          </el-form-item>
          <div class="right-btn-two">
            <!--<el-button class="btn reset-btn" @click="handleClickLogLevelCancel"> 取消 </el-button>-->
            <el-button class="btn reset-btn" @click="handleClickLogLevelSave(1)" :disabled="ganwaySetUpLog.type !== 'look'"> 查看 </el-button>
            <el-button class="btn search-btn" @click="handleClickLogLevelSave(2)" :disabled="ganwaySetUpLog.type !== 'set'"> 设置 </el-button>
          </div>
        </el-form>
        <el-form :model="ganwayLookVersion" :rules="ganwayLookVersionRules" ref="ganwayLookVersionViewForm" class="ganwaySetUpViewForm">
          <h3>
            <span>查看版本号</span>
          </h3>
          <el-form-item label="实例信息：" prop="instanceTag">
            <el-select placeholder="请选择实例信息" v-model="ganwayLookVersion.instanceTag">
              <el-option v-for="(item,index) in viewGatewayStatusList" :key="index" :label="item.zuulInstanceId" :value="item.zuulInstanceId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="版本号：" v-show="versionTag !== ''">
            <el-input :disabled="true" type="text" v-model.trim="versionTag" auto-complete="off"></el-input>
          </el-form-item>
          <div class="right-btn-two">
            <el-button class="btn reset-btn" @click="handleClickLookVersion"> 取消 </el-button>
            <el-button class="btn search-btn" @click="getVersionTag"> 查看 </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GanwaySetUpPage',
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
      }
      callback()
    }
    return {
      loadingRefresh: false,
      isCreate: '',
      showEditRouteStrategy: false,
      currentGateway: '',
      warnningEmail: '',
      versionTag: '', // 版本号查看  版本号变量
      logLevelTag: '', // 日志级别变量
      ganwayLookVersion: {
        instanceTag: ''
      },
      ganwaySetUpViewModel: {
        warnningEmail: ''
      },
      ganwaySetUpLogTotal: {
        ipport: '',
        name: '',
        level: '',
        type: ''
      },
      ganwaySetUpLog: {
        ipport: '',
        name: '',
        level: '',
        nameLevel: '',
        type: 'look'
      },
      pathNameList: ['ROOT', '127.0.0.1'],
      pathLevelList: ['DEBUG', 'INFO '],
      ganwaySetUpViewModelRules: {
        warnningEmail: [this.$validator.required('请输入预警邮箱!'), { validator: checkBlur, trigger: 'blur' }]
      },
      ganwaySetUpLogRules: {
        ipport: [this.$validator.required('请选择实例信息!'), { validator: checkBlur, trigger: 'blur' }],
        nameLevel: [this.$validator.required('请选择Path内容!'), { validator: checkBlur, trigger: 'blur' }],
        type: [this.$validator.required('请选择操作类型!'), { validator: checkBlur, trigger: 'blur' }]
      },
      ganwayLookVersionRules: {
        instanceTag: [this.$validator.required('请选择实例信息!'), { validator: checkBlur, trigger: 'blur' }]
      },
      viewGatewayStatusList: [] // ipport  列表信息
    }
  },
  watch: {
    'ganwaySetUpLog.type': function (newVal) {
      this.logLevelTag = ''
      this.ganwaySetUpLog.name = ''
      this.ganwaySetUpLog.level = ''
    },
    'ganwaySetUpLog.name': function (newVal) {
      if (this.ganwaySetUpLog.type === 'look') {
        this.ganwaySetUpLog.nameLevel = newVal !== '' ? 'hide' : ''
      } else {
        this.ganwaySetUpLog.nameLevel = (newVal !== '' && this.ganwaySetUpLog.level !== '') ? 'hide' : ''
      }
      this.$refs.ganwaySetUpLogViewForm.validate('nameLevel')
    },
    'ganwaySetUpLog.level': function (newVal) {
      this.ganwaySetUpLog.nameLevel = (newVal !== '' && this.ganwaySetUpLog.name !== '') ? 'hide' : ''
      this.$refs.ganwaySetUpLogViewForm.validate('nameLevel')
    }
  },
  created () {
    this.gatEmailList()
    this.getGatewayStatusList()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.gatEmailList()
      this.$refs.ganwaySetUpViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    // 获取ipport  list
    getGatewayStatusList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/queryZuulListInfo', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.viewGatewayStatusList = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickLogLevelSave: function (val) {
      let self = this
      let paramsObj = {
        ipport: this.ganwaySetUpLog.ipport,
        path: '/' + this.ganwaySetUpLog.name + (val === 1 ? '' : ('/' + this.ganwaySetUpLog.level))
      }
      this.$refs.ganwaySetUpLogViewForm.validate(valid => {
        if (valid) {
          this.$refs.ganwaySetUpLogViewForm.clearValidate()
          self.$http.post(self.$api.getApiAddress('/loglevel', 'CESHI_API_HOST'), paramsObj).then((res) => {
            if (res.data.code === 200) {
              this.logLevelTag = res.data.response.level
              self.$message({ message: val === 1 ? '获取成功' : '设置成功', type: 'success' })
            } else {
              self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
            }
          }).catch((err) => {
            self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
          })
        }
      })
    },
    getVersionTag: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getversion', 'CESHI_API_HOST'), {
        ipport: this.ganwayLookVersion.instanceTag
      }).then(res => {
        if (res.data.code === 200) {
          this.versionTag = res.data.response
        } else {
          self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
        }
      }).catch(() => {
        self.$message({ message: '获取版本号失败', type: 'error' })
      })
    },
    handleClickLookVersion: function () {
      this.versionTag = ''
      this.ganwayLookVersion.instanceTag = ''
      this.$refs.ganwaySetUpLogViewForm.clearValidate()
    },
    handleClickLogLevelCancel: function () {
      this.ganwaySetUpLog.ipport = ''
      this.ganwaySetUpLog.name = ''
      this.ganwaySetUpLog.level = ''
      this.logLevelTag = ''
      this.$refs.ganwaySetUpLogViewForm.clearValidate()
    },
    getLogLevelSave: function () {
      let self = this
      if (!this.showEditRouteStrategy) {
        let paramsObj = {
          emailAddress: self.ganwaySetUpViewModel.warnningEmail
        }
        this.$refs.ganwaySetUpViewForm.validate(valid => {
          if (valid) {
            self.$http.post(self.$api.getApiAddress('/saveAlarmEmailSetting', 'CESHI_API_HOST'), paramsObj).then(res => {
              if (res.data.code === 200) {
                this.showEditRouteStrategy = true
                self.$message({ message: '保存成功', type: 'success' })
              } else {
                self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
              }
            }).catch(() => {
              self.$message({ message: '保存失败', type: 'error' })
            })
          }
        })
      } else {
        this.showEditRouteStrategy = false
      }
    },
    initData: function (val) {
      this.ganwaySetUpViewModel = {
        warnningEmail: val
      }
      this.warnningEmail = val
      if (this.ganwaySetUpViewModel.warnningEmail!=='') {
        this.showEditRouteStrategy = true
      } else {
        this.showEditRouteStrategy = false
      }
    },
    gatEmailList: function (paramsGateway) {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getAlarmEmailSetting', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          if (res.data.response === null) {
            this.showEditRouteStrategy = false
          } else {
            self.initData(res.data.response)
          }
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickEdit: function () {
      this.showEditRouteStrategy = !this.showEditRouteStrategy
    },
    handleClickDelete: function () {
      let self = this
      self.$confirm('你确定要删除预警邮箱吗?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let paramsObj = {
          emailAddress: ''
        }
        self.$http.post(self.$api.getApiAddress('/saveAlarmEmailSetting', 'CESHI_API_HOST'), paramsObj).then(res => {
          if (res.data.code === 200) {
            self.$message({ message: '删除成功', type: 'success' })
            this.showEditRouteStrategy = !this.showEditRouteStrategy
            this.ganwaySetUpViewModel.warnningEmail = ''
          } else {
            self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
          }
        }).catch(() => {
          self.$message({ message: '删除失败', type: 'error' })
        })
      })
    },
    handleClickCancel: function () {
      if (this.warnningEmail !== '') {
        this.showEditRouteStrategy = true
        this.ganwaySetUpViewModel.warnningEmail = this.warnningEmail
      } else {
        this.showEditRouteStrategy = false
        this.ganwaySetUpViewModel.warnningEmail = ''
        this.$refs.ganwaySetUpViewForm.clearValidate()
      }
    },
    handleClickEmailSave: function () {
      let self = this
      if (!this.showEditRouteStrategy) {
        let paramsObj = {
          emailAddress: self.ganwaySetUpViewModel.warnningEmail
        }
        this.$refs.ganwaySetUpViewForm.validate(valid => {
          if (valid) {
            self.$http.post(self.$api.getApiAddress('/saveAlarmEmailSetting', 'CESHI_API_HOST'), paramsObj).then(res => {
              if (res.data.code === 200) {
                this.showEditRouteStrategy = true
                self.$message({ message: '保存成功', type: 'success' })
              } else {
                self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
              }
            }).catch(() => {
              self.$message({ message: '保存失败', type: 'error' })
            })
          }
        })
      } else {
        this.showEditRouteStrategy = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/ganway-set-up.page.less";
</style>
<style lang="less">
@import "../styles/ganway-set-up.reset.less";
</style>
