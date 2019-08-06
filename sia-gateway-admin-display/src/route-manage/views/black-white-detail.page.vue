<template>
  <div class="dispatch-system-default black-white-detail-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">{{$route.query.id}} ~ 黑白名单管理</span>
        <!--<i :class="['el-icon-refresh','refresh-icon',{'loading-refresh':loadingRefresh}]" @click="showHiddenRefreshTaskList"></i>-->
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="delete-btn" @click="handleClickDelete" v-show="showEditRouteStrategy"> 删除 </el-button>
        <el-button class="back-btn" @click="handleClickReturn"><img src="../images/back-icon.png" alt="" class="img">返回 </el-button>
      </div>
      <div class="section-content">
        <el-form :model="nameListViewModel" :rules="nameListViewModelRules" ref="nameListViewForm" class="nameListViewForm">
          <h3>
            <span>路由使用策略</span>
            <el-button class="delete-btn" v-show="showEditRouteStrategy" @click="handleClickEdit('1')"> 编辑 </el-button>
          </h3>
          <el-form-item label="路由策略：" prop="routerStrategy" class="radio-text">
            <el-radio-group v-model="nameListViewModel.routerStrategy" :disabled="showEditRouteStrategy">
                <el-radio label="white">白名单</el-radio>
                <el-radio label="black">黑名单</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="是否发布：" prop="isRelease"  class="radio-text">
            <el-radio-group v-model="nameListViewModel.isRelease" :disabled="showEditRouteStrategy">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <h3 class="two-h3">
            <span>{{nameTagTitle}}</span>
          </h3>
          <div>
            <el-form-item :label="passStrategyText" prop="passStrategy">
              <span v-if="showEditRouteStrategy">{{nameListViewModel.passStrategy}}</span>
              <el-select v-else placeholder="请输入通行策略" v-model="nameListViewModel.passStrategy">
                <el-option v-for="(item,index) in gatewayClusterList" :key="index" :label="item.strategyName" :value="item.strategyName"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="passAdress" prop="passAdress">
              <span v-if="showEditRouteStrategy">{{nameListViewModel.passAdress}}</span>
              <el-input v-else type="text" v-model.trim="nameListViewModel.passAdress" auto-complete="off" placeholder="请选择通行地址"></el-input>
            </el-form-item>
            <el-form-item label="描述：" prop="describe">
              <span v-if="showEditRouteStrategy">{{nameListViewModel.describe}}</span>
              <el-input v-else type="text" v-model="nameListViewModel.describe" auto-complete="off" placeholder="请输入描述内容"></el-input>
            </el-form-item>
          </div>
          <el-form-item class="save-btn-form-two" v-show="!showEditRouteStrategy">
            <el-button @click="handleClickCancel"> 取消 </el-button>
            <el-button @click="handleClickSave"> 保存 </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlackWhiteDetailPage',
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'passAdress':
          value = value.replace(/\s+/g, '')
          if (value === '') {
            return callback(new Error('请选择通行地址!'))
          }
          if (value.indexOf('，') !== -1) {
            return callback(new Error('通行地址中不能有中文状态下的 " ， " !'))
          }
          if (!new RegExp(/^((2(5[0-5]{1}|[0-4]\d{1})|[0-1]?\d{1,2})(\.(2(5[0-5]{1}|[0-4]\d{1})|[0-1]?\d{1,2})){3}[,-]?){1,}$/).test(value)) {
            return callback(new Error('通行地址应该为0.0.0.0到255.255.255.255!'))
          }
          if (new RegExp(/-$/).test(value)) {
            return callback(new Error('通行地址中不能以" - "结束!'))
          }
          break
      }
      callback()
    }
    return {
      loadingRefresh: false,
      nameTagTitle: '白名单',
      passStrategyText: '通行策略：',
      passAdress: '通行地址：',
      showEditRouteStrategy: false,
      showNameList: false,
      nameListViewModel: {
        routerStrategy: '',
        isRelease: '',
        passStrategy: 'IP',
        passAdress: '',
        describe: ''
      },
      isCreate: '',
      gatewayClusterList: [{
        id: '1',
        strategyName: 'IP'
      }],
      nameListViewModelRules: {
        routerStrategy: [this.$validator.required('请选择路由策略!')],
        isRelease: [this.$validator.required('请选择是否发布名单!')],
        passStrategy: [this.$validator.required('请选择通行策略!')],
        passAdress: [this.$validator.required('请输入通行地址!'), { validator: checkBlur, trigger: 'blur' }],
        describe: [this.$validator.required('请输入描述内容!')]
      }
    }
  },
  watch: {
    'nameListViewModel.isRelease': function (newVal, oldVal) {
      this.$refs.nameListViewForm.validate(this.nameListViewModel.isRelease)
    },
    'nameListViewModel.routerStrategy': function (newVal, oldVal) {
      let self = this
      this.changeNameTitle(newVal, 'white')
      self.$http.post(self.$api.getApiAddress('/queryWBList', 'CESHI_API_HOST'), {
        groupId: this.$route.query.groupId,
        routeid: this.$route.query.id
      }).then((res) => {
        if (res.data.code === 200 && res.data.response !== null) {
          if (res.data.response.type === newVal) {
            this.initData(res.data.response)
          } else {
            this.showNameList = false
            this.nameListViewModel = {
              routerStrategy: newVal,
              isRelease: '',
              passStrategy: 'IP',
              passAdress: '',
              describe: ''
            }
            this.initCheck()
          }
        }
        this.$refs.nameListViewForm.validate(this.nameListViewModel.routerStrategy)
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    }
  },
  created () {
    this.gatNameList('')
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.gatNameList('')
      this.$refs.nameListViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    initCheck: function () {
      this.$refs.nameListViewForm.validate(this.nameListViewModel.routerStrategy)
      this.$refs.nameListViewForm.validate(this.nameListViewModel.isRelease)
      this.$refs.nameListViewForm.validate(this.nameListViewModel.passStrategy)
      this.$refs.nameListViewForm.validate(this.nameListViewModel.passAdress)
      this.$refs.nameListViewForm.validate(this.nameListViewModel.describe)
    },
    initData: function (val, type) {
      this.nameListViewModel = {
        routerStrategy: val.type,
        isRelease: val.enabled,
        passStrategy: val.strategy,
        passAdress: val.list,
        describe: val.desc
      }
    },
    changeNameTitle: function (val, check) {
      this.nameTagTitle = val === check ? '白名单' : '黑名单'
      this.passStrategyText = val === check ? '通行策略：' : '拦截策略：'
      this.passAdress = val === check ? '通行地址：' : '拦截地址：'
    },
    gatNameList: function (type) {
      let self = this
      self.$http.post(self.$api.getApiAddress('/queryWBList', 'CESHI_API_HOST'), {
        groupId: this.$route.query.groupId,
        routeid: this.$route.query.id
      }).then((res) => {
        if (res.data.code === 200) {
          if (res.data.response === null) {
            this.showEditRouteStrategy = false
            this.showNameList = false
            this.isCreate = 'create'
          } else {
            this.showEditRouteStrategy = true
            this.showNameList = true
            this.isCreate = 'edit'
            this.initData(res.data.response, type)
          }
        }
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
    handleClickEdit: function (index) {
      if (index === '1') {
        this.showEditRouteStrategy = false
      } else {
        this.showNameList = false
      }
    },
    handleClickDelete: function () {
      let self = this
      self.$confirm('你确定要删除' + this.$route.query.id + '路由的名单吗?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let paramsObj = {
          groupId: this.$route.query.groupId,
          routeid: this.$route.query.id,
          type: self.nameListViewModel.routerStrategy,
          enabled: self.nameListViewModel.isRelease,
          strategy: self.nameListViewModel.passStrategy,
          list: self.nameListViewModel.passAdress,
          desc: self.nameListViewModel.describe
        }
        self.$http.post(self.$api.getApiAddress('/deleteWBList2Route', 'CESHI_API_HOST'), paramsObj).then(res => {
          if (res.data.code === 200) {
            self.$message({ message: '删除成功', type: 'success' })
            this.nameListViewModel = {
              routerStrategy: '',
              isRelease: '',
              passStrategy: 'IP',
              passAdress: '',
              describe: ''
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
    handleClickCancel: function () {
      this.nameListViewModel = {
        routerStrategy: '',
        isRelease: '',
        passStrategy: 'IP',
        passAdress: '',
        describe: ''
      }
      this.$refs.nameListViewForm.clearValidate()
    },
    handleClickSave: function () {
      let self = this
      let requestPath = this.isCreate === 'create' ? '/addWBList2Route' : '/updateWBList2Route'
      let paramsObj = {
        groupId: this.$route.query.groupId,
        routeid: this.$route.query.id,
        type: self.nameListViewModel.routerStrategy,
        enabled: self.nameListViewModel.isRelease,
        strategy: self.nameListViewModel.passStrategy,
        list: self.nameListViewModel.passAdress,
        desc: self.nameListViewModel.describe
      }
      this.$refs.nameListViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress(requestPath, 'CESHI_API_HOST'), paramsObj).then(res => {
            if (res.data.code === 200) {
              self.$message({ message: '保存成功', type: 'success' })
              self.clearAction('return')
            } else {
              self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
            }
          }).catch(() => {
            self.$message({ message: '保存失败', type: 'error' })
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
@import "../styles/black-white-detail.page.less";
</style>
<style lang="less">
@import "../styles/black-white-detail.page.reset.less";
</style>
