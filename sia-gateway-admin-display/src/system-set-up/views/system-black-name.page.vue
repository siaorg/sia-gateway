<template>
  <div class="dispatch-system-default black-white-detail-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">系统黑名单</span>
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="delete-btn" @click="handleClickDelete" v-show="showEditRouteStrategy"> 删除 </el-button>
        <el-button class="delete-btn" @click="handleClickEdit" v-show="showEditRouteStrategy"> 编辑 </el-button>
      </div>
      <div class="section-content">
        <el-form :model="routeCreateViewModel" :rules="routeCreateViewModelRules" ref="routeCreateViewForm" class="routeCreateViewForm">
          <!--<h3>
            <span>系统黑名单</span>
            <el-button class="small-edit" @click="handleClickEdit" v-show="showEditRouteStrategy"> 编辑 </el-button>
          </h3>-->
          <el-form-item label="拦截策略：" prop="passStrategy">
            <span v-if="showEditRouteStrategy">{{routeCreateViewModel.passStrategy}}</span>
            <el-select v-else placeholder="请选择拦截策略" v-model="routeCreateViewModel.passStrategy">
              <el-option v-for="(item,index) in gatewayClusterList" :key="index" :label="item.strategyName" :value="item.strategyName"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="拦截地址：" prop="passAdress">
            <span v-if="showEditRouteStrategy">{{routeCreateViewModel.passAdress}}</span>
            <el-input v-else type="text" v-model.trim="routeCreateViewModel.passAdress" auto-complete="off" placeholder="请输入拦截地址"></el-input>
          </el-form-item>
          <el-form-item label="描述：" prop="describe">
            <span v-if="showEditRouteStrategy">{{routeCreateViewModel.describe}}</span>
            <el-input v-else type="text" v-model="routeCreateViewModel.describe" auto-complete="off" placeholder="请输入描述内容"></el-input>
          </el-form-item>
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
  name: 'SystemBlackNamePage',
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'passStrategy':
          if (value === '') {
            return callback(new Error('请选择通行策略!'))
          }
          break
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
        case 'describe':
          if (value === '') {
            return callback(new Error('请输入描述内容!'))
          }
          break
      }
      callback()
    }
    return {
      loadingRefresh: false,
      isCreate: '',
      showEditRouteStrategy: false,
      currentGateway: '',
      routeCreateViewModel: {
        passStrategy: 'IP',
        passAdress: '',
        describe: ''
      },
      gatewayClusterList: [{
        id: '1',
        strategyName: 'IP'
      }],
      routeCreateViewModelRules: {
        passStrategy: [this.$validator.required('请选择通行策略!'), { validator: checkBlur, trigger: 'blur' }],
        passAdress: [this.$validator.required('请输入通行地址!'), { validator: checkBlur, trigger: 'blur' }],
        describe: [this.$validator.required('请输入描述内容!'), { validator: checkBlur, trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getCurrentGanteway()
  },
  methods: {
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getCurrentGanteway()
      this.$refs.routeCreateViewForm.clearValidate()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    initData: function (val) {
      this.routeCreateViewModel = {
        passStrategy: val.strategy,
        passAdress: val.list,
        describe: val.desc
      }
    },
    getCurrentGanteway: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getZuulGgroupName', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.currentGateway = res.data.response
          self.gatNameList(res.data.response)
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    gatNameList: function (paramsGateway) {
      let self = this
      self.$http.post(self.$api.getApiAddress('/queryWBList', 'CESHI_API_HOST'), {
        groupId: paramsGateway,
        routeid: 'globalBlackServiceId'
      }).then((res) => {
        if (res.data.code === 200) {
          if (res.data.response === null) {
            this.isCreate = 'create'
            this.showEditRouteStrategy = false
          } else {
            this.isCreate = 'edit'
            self.initData(res.data.response)
            this.showEditRouteStrategy = true
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
      self.$confirm('你确定要删除系统黑名单吗?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let paramsObj = {
          groupId: self.currentGateway,
          routeid: 'globalBlackServiceId',
          type: 'black',
          enabled: true,
          strategy: self.routeCreateViewModel.passStrategy,
          list: self.routeCreateViewModel.passAdress,
          desc: self.routeCreateViewModel.describe
        }
        self.$http.post(self.$api.getApiAddress('/deleteWBList2Route', 'CESHI_API_HOST'), paramsObj).then(res => {
          if (res.data.code === 200) {
            self.$message({ message: '删除成功', type: 'success' })
            this.showEditRouteStrategy = !this.showEditRouteStrategy
            this.routeCreateViewModel = {
              passStrategy: 'IP',
              passAdress: '',
              describe: ''
            }
          } else {
            self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
          }
        }).catch(() => {
          self.$message({ message: '删除失败', type: 'error' })
        })
      })
    },
    handleClickCancel: function () {
      this.routeCreateViewModel = {
        passStrategy: 'IP',
        passAdress: '',
        describe: ''
      }
      this.$refs.routeCreateViewForm.clearValidate()
    },
    handleClickSave: function () {
      let self = this
      let requestPath = this.isCreate === 'create' ? '/addWBList2Route' : '/addWBList2Route'
      let paramsObj = {
        groupId: self.currentGateway,
        routeid: 'globalBlackServiceId',
        type: 'black',
        enabled: true,
        strategy: self.routeCreateViewModel.passStrategy,
        list: self.routeCreateViewModel.passAdress,
        desc: self.routeCreateViewModel.describe
      }
      this.$refs.routeCreateViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress(requestPath, 'CESHI_API_HOST'), paramsObj).then(res => {
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
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/system-black-name.page.less";
</style>
<style lang="less">
@import "../styles/system-black-name.page.reset.less";
</style>
