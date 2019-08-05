<template>
  <div class="dispatch-system-default safe-auth-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">{{$route.query.id}} ~ 安全认证</span>
        <!--<i :class="['el-icon-refresh','refresh-icon',{'loading-refresh':loadingRefresh}]" @click="showHiddenRefreshTaskList"></i>-->
        
        <!--<el-button class="delete-button" @click="handleClickDelete"> 删除 </el-button>-->
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="edit-btn" @click="handleClickEdit" :disabled="routeCreateViewModel.endTime==='' && routeCreateViewModel.sefePassword===''"> 编辑 </el-button>
        <el-button class="back-btn" @click="handleClickReturn"><img src="../images/back-icon.png" alt="" class="img">返回 </el-button>
      </div>
      <div class="section-content">
        <el-form :model="routeCreateViewModel" :rules="routeCreateViewModelRules" ref="routeCreateViewForm" class="routeCreateViewForm">
          <el-form-item label="安全口令：" prop="sefePassword">
            <el-input :disabled="isSafeValueEmpty" type="password" v-model="routeCreateViewModel.sefePassword" auto-complete="off" placeholder="请输入安全口令"></el-input>
          </el-form-item>
          <div>
            <el-col>
              <el-form-item label="有效时间：" prop="endTime">
                <el-date-picker
                  v-model="routeCreateViewModel.startTime"
                  type="datetime"
                  :disabled="true"
                  placeholder="开始日期">
                </el-date-picker>
                <span class="start-end">~</span>
                <el-date-picker
                  :disabled="isSafeValueEmpty"
                  v-model="routeCreateViewModel.endTime"
                  :picker-options="pickerOptions"
                  type="datetime"
                  placeholder="结束日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <div style="clear:both;"></div>
          </div>
          <el-form-item class="save-btn-form">
            <el-button @click="handleClickSave" v-show="!isSafeValueEmpty"> 添加认证 </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RouteSafeAuthPage',
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'sefePassword':
          if (value.replace(/(^\s*)|(\s*$)/g, '') !== '' && !new RegExp(/^[^]{1,50}$/).test(value)) {
            return callback(new Error('输入内容必须是1到50个字符!'))
          }
          break
        case 'endTime':
          if (Date.parse(new Date()) > Date.parse(new Date(value))) {
            return callback(new Error('结束时间不能小于或等于开始时间!'))
          }
          break
      }
      callback()
    }
    return {
      loadingRefresh: false,
      isSafeValueEmpty: false,
      routeCreateViewModel: {
        sefePassword: '',
        startTime: '',
        endTime: ''
      },
      routeCreateViewModelRules: {
        sefePassword: [this.$validator.required('请输入安全口令！'), { validator: checkBlur, trigger: 'blur' }],
        endTime: [this.$validator.required('请输入有效时间！'), { validator: checkBlur, trigger: 'blur' }]
      },
      pickerOptions: {
        disabledDate (time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      }
    }
  },
  watch: {
    'routeCreateViewModel.endTime': function (newVal, oldVal) {
      if (!this.isSafeValueEmpty) {
        this.routeCreateViewModel.startTime = this.$formatDate.dateFormat(new Date())
      }
    }
  },
  created () {
    this.getSafeAuthInfo()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getSafeAuthInfo()
      this.$refs.routeCreateViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    handleClickEdit: function () {
      this.isSafeValueEmpty = !this.isSafeValueEmpty
    },
    handleClickDelete: function () {
      let self = this
      self.$confirm('你确定要删除' + this.$route.query.id + '路由的安全口令么?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        self.$http.post(self.$api.getApiAddress('/deleteAuth', 'CESHI_API_HOST'), {
          routeid: this.$route.query.id
        }).then(res => {
          if (res.data.code === 200) {
            self.$message({ message: '删除成功', type: 'success' })
            this.isSafeValueEmpty = false
            this.routeCreateViewModel = {
              sefePassword: '',
              endTime: '',
              startTime: this.$formatDate.dateFormat(new Date())
            }
            self.clearAction('return')
          } else {
            self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
          }
        }).catch(() => {
          self.$message({ message: '删除失败', type: 'error' })
        })
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
    getSafeAuthInfo: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/queryAuth', 'CESHI_API_HOST'), {
        routeid: this.$route.query.id
      }).then(res => {
        if (res.data.response !== null) {
          this.routeCreateViewModel = {
            sefePassword: res.data.response.client_secret,
            endTime: res.data.response.endTime,
            startTime: res.data.response.startTime
          }
          this.isSafeValueEmpty = true
        } else {
          this.routeCreateViewModel.startTime = this.$formatDate.dateFormat(new Date())
          this.isSafeValueEmpty = false
        }
      }).catch(() => {
        this.isSafeValueEmpty = false
        self.$message({ message: '获取失败', type: 'error' })
      })
    },
    handleClickSave: function () {
      let self = this
      let paramsObj = {
        'routeid': this.$route.query.id,
        'client_secret': self.routeCreateViewModel.sefePassword,
        'startTime': this.$formatDate.dateFormat(this.routeCreateViewModel.startTime),
        'endTime': this.$formatDate.dateFormat(this.routeCreateViewModel.endTime)
      }
      this.$refs.routeCreateViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('/saveAuth', 'CESHI_API_HOST'), paramsObj).then(res => {
            if (res.data.code === 200) {
              self.$message({ message: '添加成功', type: 'success' })
              self.clearAction('return')
            } else {
              self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
            }
          }).catch(() => {
            self.$message({ message: '添加失败', type: 'error' })
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
@import "../styles/route-safe-auth.page.less";
</style>
<style lang="less">
@import "../styles/route-safe-auth.page.reset.less";
</style>
