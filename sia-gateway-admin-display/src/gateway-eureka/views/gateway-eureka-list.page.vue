<template>
    <div class="dispatch-system-default gateway-eureka-list-page">
      <div class="section-container">
        <div class="section-header">
          <div class="search-box">
            <div class="search-list">
              <span class="title">Application</span>
              <el-input type="text" auto-complete="off" placeholder="请输入Application" v-model="applyValue"></el-input>
              <el-button class="btn search-btn" @click="handleClickSearchRoute"> 查询 </el-button>
            </div>
            <div class="search-list" v-if="JSON.stringify(urlsList) !== '[]'">
              <span class="title">当前Eureka</span>
              <el-input v-if="urlsList.length === 1" type="text" auto-complete="off" placeholder="请输入Application" v-model="urlsList[0]" :disabled="true"></el-input>
              <el-popover
                v-if="urlsList.length > 1"
                placement="bottom"
                width="310"
                popper-class="tooltip"
                trigger="hover">
                <div class="tooltip-box">
                  <p v-for="(item, index) in urlsList" :key="index"> {{item}}</p>
                </div>
                <el-input type="text" slot="reference" auto-complete="off" v-model="urlsValue" :disabled="true"></el-input>
              </el-popover>
            </div>
          </div>
        </div>
        <div class="bg-empty"></div>
        <div class="section-content">
          <div class="btn-box">
            <el-button class="refresh-btn" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
            <el-button class="btn search-btn" @click="handleClickSetEureka">设置Euraka</el-button>
            <el-button class="btn create-btn" @click="handleClickResetEureka" :loading="isDisableResetEureka"> {{isDisableResetEureka?'重置中':'重置Euraka'}} </el-button>
          </div>
          <el-table :data="viewEurekaList" border style="width: 100%" class="task-manage-table">
            <el-table-column prop="name" label="GrounpName"  show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.appName.split('-')[0]}}
              </template>
            </el-table-column>
            <el-table-column prop="appName" label="Application" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="instanceName" label="IP:PROT" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="status" label="状态" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.status}}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <!-- 设置eureka  start -->
      <set-eureka-tmpl  v-if="isShowSetEurekaTmpl" v-on:showHiddenSetEurekaTmpl="showHiddenSetEurekaTmpl">
      </set-eureka-tmpl>
      <!-- 设置eureka  end -->
      <!-- 重置eureka  start -->
      <reset-eureka-tmpl  v-if="isShowResetEurekaTmpl" v-on:showHiddenResetetEurekaTmpl="showHiddenResetetEurekaTmpl">
      </reset-eureka-tmpl>
      <!-- 重置eureka  end -->
    </div>
</template>

<script>
const setEurekaTmpl = resolve => require(['../components/set-eureka.tmpl.vue'], resolve)
const resetEurekaTmpl = resolve => require(['../components/reset-eureka.tmpl.vue'], resolve)
export default {
  name: 'gatewayEurekaListPage',
  components: {setEurekaTmpl, resetEurekaTmpl},
  data () {
    return {
      // 设置eureka
      isShowSetEurekaTmpl: false,
      setEurekaTmplParams: '',
      // 重置eureka
      isShowResetEurekaTmpl: false,
      loadingRefresh: false,
      applyValue: '',
      viewEurekaList: [],
      viewEurekaSearchList: [],
      urlsList: [],
      urlsValue: '',
      isDisableResetEureka: false
    }
  },
  created () {
    this.getGatewayAuditList()
    this.getUrlsList()
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
    getUrlsList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getEurekaUrls', 'CESHI_API_HOST'), {
        'zuulGroupName': this.$store.state.frame.currentGatewayGroup
      }).then(res => {
        if (res.data.code === 200) {
          self.urlsList = res.data.response
          self.urlsValue = res.data.response[0] + '...'
        }
      }).catch((err) => {
        self.$message({ message: '获取URLS失败', type: 'error' })
      })
    },
    getGatewayAuditList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getAllServiceStatus', 'CESHI_API_HOST'), {
        zuulGroupName: this.$store.state.frame.currentGatewayGroup
      }).then((res) => {
        if (res.data.code === 200) {
          let list = []
          Object.keys(res.data.response).forEach((ele) => {
            if (Object.keys(res.data.response[ele]).length > 1) {
              Object.keys(res.data.response[ele]).forEach((elecheck) => {
                list.push(
                  {
                    appName: ele,
                    instanceName: elecheck,
                    status: res.data.response[ele][elecheck]
                  }
                )
              })
            } else {
              list.push(
                {
                  appName: ele,
                  instanceName: Object.keys(res.data.response[ele])[0],
                  status: res.data.response[ele][Object.keys(res.data.response[ele])[0]]
                }
              )
            }
          })
          self.viewEurekaList = list
          self.viewEurekaSearchList = list
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickSearchRoute: function () {
      let self = this
      let arrList = []
      if (this.viewEurekaSearchList.length === 0) {
        this.viewEurekaList = arrList
        return false
      }
      this.viewEurekaSearchList.forEach(function (element) {
        let isSearchValue = element.appName.toUpperCase().indexOf(self.applyValue.toUpperCase())
        if (isSearchValue !== -1 && JSON.stringify(arrList).indexOf(element) === -1) {
          arrList.push(element)
        }
      })
      this.viewEurekaList = arrList
    },
    handleClickSetEureka: function () {
      this.isShowSetEurekaTmpl = true
    },
    handleClickResetEureka: function () {
      // this.isShowResetEurekaTmpl = true
      let self = this
      self.$confirm('你确定要重置当前Eureka吗?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.isDisableResetEureka = true
        self.$http.post(self.$api.getApiAddress('/resetEurekaUrls?zuulGroupName=' + this.$store.state.frame.currentGatewayGroup, 'CESHI_API_HOST'), {
        }).then(res => {
          this.isDisableResetEureka = false
          if (res.data.code === 200 && typeof(res.data.response) !== 'string') {
            self.$message({message: res.data.response, type: 'success'})
          } else {
            self.$message({ message: res.data.response, type: 'error' })
          }
        }).catch((err) => {
          this.isDisableResetEureka = false
          self.$message({ message: '重置失败', type: 'error' })
        })
      })
    },
    showHiddenSetEurekaTmpl: function (val) {
      this.isShowSetEurekaTmpl = val
    },
    showHiddenResetetEurekaTmpl: function (val) {
      this.isShowResetEurekaTmpl = val
    }
  }
}
</script>
<style lang="less">
@import "../styles/gateway-eureka-list.page.reset.less";
</style>
<style lang="less" scoped>
@import "../styles/gateway-eureka-list.page.less";
</style>
