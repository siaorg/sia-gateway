<template>
    <div class="dispatch-system-default task-manage-list-page">
      <div class="section-container">
        <div class="section-header">
          <div class="search-box">
            <div class="search-list">
              <span>路由 ID</span>
              <el-input type="text" auto-complete="off" placeholder="请输入路由ID" v-model="routeName"></el-input>
            </div>
            <div class="search-list">
              <span>匹配路径</span>
              <el-input class="ml" type="text" auto-complete="off" placeholder="请输入匹配路径" v-model="configPath"></el-input>
            </div>
            <div class="search-list">
              <span>后端服务</span>
              <el-input class="ml" type="text" auto-complete="off" placeholder="请输入后端服务URL" v-model="serverUrl"></el-input>
            </div>
            <div class="search-list">
              <span>应用名称</span>
              <el-select placeholder="请选择应用名称" v-model="applyListTag" class="ml" filterable allow-create>
                <el-option v-for="(item,index) in applyList" :key="index" :label="item.name" :value="item.name"></el-option>
              </el-select>
            </div>
            <div class="search-list">
              <el-button class="btn search-btn ml-span" @click="handleClickSearchRoute"> 查询 </el-button>
              <el-button class="btn reset-btn" @click="handleClickResetSearchInfo"> 重置 </el-button> 
            </div>
          </div>
        </div>
        <div class="bg-empty"></div>
        <div class="section-content">
          <div class="btn-box">
            <el-button class="refresh-btn" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
            <el-button class="btn search-btn" @click="downloadRouteList">路由导出</el-button>
            <el-button class="btn search-btn" v-if="uploadGroupLoading" :loading="uploadGroupLoading" :disabled="!uploadGroupLoading">上传中</el-button>
            <el-upload
              class="upload-demo"
              :action="$api.getApiAddress('/uploadRouteFile', 'CESHI_API_HOST')"
              name="file"
              v-if="!uploadGroupLoading"
              :data="uploadParams"
              :on-change="uploadChangeAddParams"
              :on-success="handleFileSuccess"
              :on-error="handleFileError"
              :before-upload="beforeAvatarUpload"
              :auto-upload="true"
              :show-file-list="false">
              <el-button class="btn search-btn">路由导入</el-button>
            </el-upload>
            <el-button class="btn create-btn" @click="handleClickAddRoute"> 新建路由 </el-button>
          </div>
          <el-table :data="viewLogManageList" border style="width: 100%" class="task-manage-table" @selection-change="handleSelectionChange">
            <el-table-column
              type="selection"
              width="55">
            </el-table-column>
            <el-table-column prop="id" label="路由拓扑图" width="90">
              <template slot-scope="scope">
                <span class="router-link" @click="showRouterLevel(scope.row)"><img src="../images/tuopo.svg" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="routeid" label="路由ID" width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                <span>{{scope.row.routeid}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="apiName" label="应用名称" width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.apiName === 'null' ? '' : scope.row.apiName}}
              </template>
            </el-table-column>
            <el-table-column prop="path" label="匹配路径" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="serviceId" label="后端服务ID" width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.serviceId === 'null' ? '' : scope.row.serviceId}}
              </template>
            </el-table-column>
            <el-table-column prop="url" label="后端服务URL" min-width="160" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.url === 'null' ? '' : scope.row.url}}
              </template>
            </el-table-column>
            <el-table-column prop="retryable" label="路由策略" width="130" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.strategy}}
              </template>
            </el-table-column>
            <el-table-column prop="stripPrefix" label="前缀是否生效" align="center" width="100" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.stripPrefix ? '是' : '否'}}
              </template>
            </el-table-column>
            <el-table-column prop="routeStatus" label="Route状态" align="center" width="95" show-overflow-tooltip>
              <template slot-scope="scope">
                {{scope.row.routeStatus | filterStatus}}
              </template>
            </el-table-column>
            <el-table-column prop="jobKey" label="操作" align="center" min-width="130" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-dropdown class="list-select" @command="handleSelectOpear" @visible-change="handleChangeOpear(scope.row,$event)">
                  <span class="el-dropdown-link">
                    选项操作<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="(item, index) in routerOpeartMoreList" :disabled="item.disable" :key="index" :command="composeValue(item.opearTag, scope.row)">{{item.opearTag}}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown> 
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-pagination v-show="pageCount!=0" layout="prev, pager, next" :page-count="pageCount" :current-page="currentPageIndex" :page-size="pageSize" @current-change="handleCurrentChange">
        </el-pagination>
      </div>
      <!-- 蓝绿部署组件  start -->
      <dispose-subgroup-tmpl  v-if="disposeSubgroupShow" :disposeSubgroupParams="disposeSubgroupParams" v-on:showHiddenDisposeSubgroup="showHiddenDisposeSubgroup">
      </dispose-subgroup-tmpl>
      <!-- 蓝绿部署组件  end -->
      <!-- 限流组件  start -->
      <current-limit-group-tmpl  v-if="currentLimitGroupShow" :currentLimitGroupParams="currentLimitGroupParams" v-on:showHiddenCurrentLimitGroup="showHiddenCurrentLimitGroup">
      </current-limit-group-tmpl>
      <!-- 限流组件  end -->
      <!-- 路由组件  start -->
      <group-list-tmpl  v-if="groupListShow" :groupListParams="groupListParams" v-on:showHiddenGroupList="showHiddenGroupList">
      </group-list-tmpl>
      <!-- 路由组件  end -->
      <!-- 路由组件  start -->
      <unload-situat-tmpl  v-if="routerUpdateShow" :routerUpdate="routerUpdate" v-on:showHiddenRouterUpdate="showHiddenRouterUpdate">
      </unload-situat-tmpl>
      <!-- 路由组件  end -->
      <!-- 路由关系  start -->
      <router-level-tmpl  v-if="routerLevleShow" :routerLevelParams="routerLevelParams" v-on:showHiddenRouterLevel="showHiddenRouterLevel">
      </router-level-tmpl>
      <!-- 路由关系  end -->
      <!-- 金丝雀组件  start -->
      <gray-public-tmpl  v-if="grayPublicShow" :grayPublicParams="grayPublicParams" v-on:showHiddenGrayPublicParams="showHiddenGrayPublicParams">
      </gray-public-tmpl>
      <!-- 金丝雀组件  end -->
    </div>
</template>

<script>
const disposeSubgroupTmpl = resolve => require(['../components/dispose-subgroup.tmpl.vue'], resolve)
const currentLimitGroupTmpl = resolve => require(['../components/current-limit-group.tmpl.vue'], resolve)
const groupListTmpl = resolve => require(['../components/group-list.tmpl.vue'], resolve)
const unloadSituatTmpl = resolve => require(['../components/unload-situat.tmpl.vue'], resolve)
const routerLevelTmpl = resolve => require(['../components/router-level.tmpl.vue'], resolve)
const grayPublicTmpl = resolve => require(['../components/gray-public.tmpl.vue'], resolve)
export default {
  name: 'RouteManageListPage',
  components: {disposeSubgroupTmpl, currentLimitGroupTmpl, groupListTmpl, unloadSituatTmpl, routerLevelTmpl, grayPublicTmpl},
  data () {
    return {
      // 金丝雀组件
      grayPublicShow: false,
      grayPublicParams: {},
      // 路由关系
      routerLevleShow: false,
      routerLevelParams: {},
      uploadGroupLoading: false, // 上传加载动画是否显示
      uploadParams: {}, // 上传参数
      downLoadParams: [], // 下载所需参数
      routerUpdate: {}, // 组件上传情况失败、成功
      routerUpdateShow: false, // 上传情况组件显示隐藏
      loadingRefresh: false,
      applyListTag: '',
      applyList: [],
      currentPageIndex: 1,
      pageSize: 10,
      pageCount: 0,
      disposeSubgroupShow: false,
      currentLimitGroupShow: false,
      groupListShow: false,
      currentLimitGroupParams: '',
      disposeSubgroupParams: {},
      groupListParams: '',
      routeName: '',
      configPath: '',
      serverUrl: '',
      routerOpeartMoreList: [
        {
          opearTag: '发布',
          disable: false
        },
        {
          opearTag: '下线',
          disable: false
        },
        {
          opearTag: '删除',
          disable: false
        },
        {
          opearTag: '修改',
          disable: false
        },
        {
          opearTag: '查看',
          disable: false
        },
        {
          opearTag: '黑白名单组件',
          disable: false
        },
        {
          opearTag: '统计组件',
          disable: false
        },
        {
          opearTag: '安全认证',
          disable: false
        },
        {
          opearTag: '限流组件',
          disable: false
        },
        {
          opearTag: '响应日志组件',
          disable: false
        },
        {
          opearTag: '请求日志组件',
          disable: false
        },
        {
          opearTag: '蓝绿部署组件',
          disable: false
        },
        {
          opearTag: '金丝雀组件',
          disable: false
        }
      ],
      viewLogManageList: []
    }
  },
  filters: {
    filterStatus (val) {
      switch (val) {
        case 'ONLINE':
          return '发布'
        case 'EDIT':
          return '编辑'
        case 'DOWNLINE':
          return '下线'
      }
    }
  },
  created () {
    this.getRouterList()
    this.getSearchApplyList()
    this.uploadParams = {
      currentRoleName: this.$store.state.frame.currentGatewayGroup.split('-')[0]
    }
  },
  methods: {
    // 金丝雀组件
    // showRouterLevel: function (val) {
    //   this.grayPublicParams = val
    //   this.grayPublicShow = true
    // },
    showHiddenGrayPublicParams: function (val) {
      this.grayPublicShow = val
    },
    // 路由关系查看
    showHiddenRouterLevel: function (val) {
      this.routerLevleShow = val
    },
    showRouterLevel: function (val) {
      this.routerLevelParams = val
      this.routerLevleShow = true
    },
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      this.getRouterList()
      this.getSearchApplyList()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    // 下载路由列表
    downloadRouteList: function () {
      let params = []
      this.downLoadParams.forEach((ele) => {
        params.push(ele.routeid)
      })
      this.$http.downLoad(this.$api.getApiAddress('/downloadRouteFile', 'CESHI_API_HOST'), {
        routeIdList: params.join(',')
      }).then((res) => {
        let routerListData = res.data
        let blob = new Blob([routerListData], { type: 'text/json;charset=utf-8;' })
        let link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = 'routerList.json'
        link.click()
      })
    },
    showHiddenRouterUpdate: function (val) {
      this.routerUpdateShow = val
    },
    // 选择列表数据下载
    handleSelectionChange: function (val) {
      this.downLoadParams = val
    },
    // 上传时需要的参数
    uploadChangeAddParams: function () {
      this.uploadParams = {
        currentRoleName: this.$store.state.frame.currentGatewayGroup.split('-')[0]
      }
    },
    // 上传成功函数
    handleFileSuccess: function (res, file) {
      let self = this
      if (res.code === 200) {
        this.getRouterList()
        this.routerUpdate = res.response
        this.routerUpdateShow = true
        this.uploadGroupLoading = false
        // this.$message({message: '上传成功!', type: 'success'})
      } else {
        this.uploadGroupLoading = false
        this.$message({message: '上传失败!', type: 'error'})
      }
    },
    // 上传失败函数
    handleFileError: function (res, file) {
      this.uploadGroupLoading = false
      this.$message({message: '上传失败!', type: 'error'})
    },
    // 上传前验证
    beforeAvatarUpload: function () {
      this.uploadGroupLoading = true
    },
    getSearchApplyList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getApplicationNameList', 'CESHI_API_HOST'), {
        zuulGroupName: this.$store.state.frame.currentGatewayGroup.split('-')[0]
      }).then((res) => {
        if (res.data.code === 200) {
          for (var i = 0; i < res.data.response.length; i++) {
            if (res.data.response[i].name == '空值') {
              res.data.response.splice(i, 1)
              res.data.response.unshift({
                name: '空值',
                value: ''
              })
            }
          }
          self.applyList = res.data.response
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    getRouterList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getRouteList', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.viewLogManageList = res.data.response
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    handleClickResetSearchInfo: function () {
      this.routeName = ''
      this.configPath = ''
      this.serverUrl = ''
      this.applyListTag = ''
      this.getRouterList()
    },
    handleClickSearchRoute: function () {
      let self = this
      let paramsObj = {
        routeid: this.routeName,
        path: this.configPath,
        url: this.serverUrl,
        apiName: this.applyListTag
      }
      if (this.applyListTag === '空值') {
        paramsObj.apiName = 'empty'
      }
      if (!this.routeName && !this.configPath && !this.serverUrl && !this.applyListTag) {
        this.getRouterList()
      } else {
        self.$http.post(self.$api.getApiAddress('/getRouteByPath', 'CESHI_API_HOST'), paramsObj).then(res => {
          if (res.data.code === 200) {
            self.viewLogManageList = res.data.response
            self.$message({message: '查询成功', type: 'success'})
          } else {
            self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
          }
        }).catch(() => {
          self.$message({ message: '查询失败', type: 'error' })
        })
      }
    },
    handleChangeOpear: function (row, isShow) {
      if (isShow) {
        switch (row.routeStatus) {
          case 'ONLINE':
            this.routerOpeartMoreList.forEach(function (element) {
              switch (element.opearTag) {
                case '统计组件':
                  if (JSON.stringify(row.component).indexOf('统计组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '安全认证':
                  if (JSON.stringify(row.component).indexOf('安全认证') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '限流组件':
                  if (JSON.stringify(row.component).indexOf('限流组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '请求日志组件':
                  if (JSON.stringify(row.component).indexOf('请求日志组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '响应日志组件':
                  if (JSON.stringify(row.component).indexOf('响应日志组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '蓝绿部署组件':
                  if (JSON.stringify(row.component).indexOf('蓝绿部署组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '金丝雀组件':
                  if (JSON.stringify(row.component).indexOf('金丝雀组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '黑白名单组件':
                  if (JSON.stringify(row.component).indexOf('黑白名单组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '删除':
                case '修改':
                case '发布':
                  element.disable = true
                  break
                default:
                  element.disable = false
              }
            })
            break
          case 'EDIT':
          case 'DOWNLINE':
            this.routerOpeartMoreList.forEach(function (element) {
              switch (element.opearTag) {
                case '统计组件':
                  if (JSON.stringify(row.component).indexOf('统计组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '安全认证':
                  if (JSON.stringify(row.component).indexOf('安全认证') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '限流组件':
                  if (JSON.stringify(row.component).indexOf('限流组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '请求日志组件':
                  if (JSON.stringify(row.component).indexOf('请求日志组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '响应日志组件':
                  if (JSON.stringify(row.component).indexOf('响应日志组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '蓝绿部署组件':
                  if (JSON.stringify(row.component).indexOf('蓝绿部署组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '金丝雀组件':
                  if (JSON.stringify(row.component).indexOf('金丝雀组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '黑白名单组件':
                  if (JSON.stringify(row.component).indexOf('黑白名单组件') === -1) {
                    element.disable = true
                  } else {
                    element.disable = false
                  }
                  break
                case '下线':
                  element.disable = true
                  break
                default:
                  element.disable = false
              }
            })
            break
        }
      }
    },
    handleSelectOpear: function (params) {
      console.log(params, '-----------------------------')
      let self = this
      let requestParams = ''
      switch (params.opearTag) {
        case '删除':
          requestParams = '/deleteRoute'
          break
        case '发布':
          requestParams = '/publisheRoute'
          break
        case '下线':
          requestParams = '/downRoute'
          break
      }
      switch (params.opearTag) {
        case '黑白名单组件':
          this.$router.push({path: '/black-white-detail', query: { id: params.row.routeid, groupId: params.row.zuulGroupName }})
          break
        case '删除':
          self.$confirm('你确定要删除该路由么?', '', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            self.opearStatusChange(requestParams, params.row.routeid)
          })
          break
        case '修改':
          this.$router.push({ path: '/route-edit', query: { id: params.row.routeid } })
          break
        case '查看':
          // this.groupListParams = params.row.routeid
          // this.groupListShow = true
          this.routerLevelParams = params.row
          this.routerLevleShow = true
          break
        case '统计组件':
          this.$router.push({ path: '/run-static-detail', query: { zuulGroupName: params.row.zuulGroupName, id: params.row.routeid } })
          break
        case '安全认证':
          this.$router.push({ path: '/route-safe-auth', query: { id: params.row.routeid } })
          break
        case '蓝绿部署组件':
          self.$http.post(self.$api.getApiAddress('/routeRibbonQuery', 'CESHI_API_HOST'), {
            routeid: params.row.routeid,
            serviceId: params.row.serviceId
          }).then(res => {
            if (res.data.response !== null) {
              if (res.data.response.allVersions !== '') {
                this.disposeSubgroupParams = {
                  routeid: params.row.routeid,
                  serviceId: params.row.serviceId,
                  resData: res.data.response
                }
                this.disposeSubgroupShow = true
              } else {
                self.$confirm('此路由没有配置版本信息!', '', {confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'})
              }
            } else {
              self.$confirm('此路由没有配置版本信息!', '', {confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'})
            }
          })
          break
        case '限流组件':
          this.currentLimitGroupParams = params.row.routeid
          this.currentLimitGroupShow = true
          break
        case '请求日志组件':
        case '响应日志组件':
          this.$router.push({path: '/log-manage-list', query: { id: params.row.routeid }})
          break
        case '金丝雀组件':
          console.log('-----------------------------')
          this.grayPublicParams = params.row
          this.grayPublicShow = true
          break
        default:
          self.opearStatusChange(requestParams, params.row.routeid)
          break
      }
    },
    showHiddenDisposeSubgroup: function (val) {
      this.getRouterList()
      this.disposeSubgroupShow = val
    },
    showHiddenCurrentLimitGroup: function (val) {
      this.getRouterList()
      this.currentLimitGroupShow = val
    },
    showHiddenGroupList: function (val) {
      this.groupListShow = val
    },
    opearStatusChange: function (requestParams, routeid) {
      let self = this
      self.$http.post(self.$api.getApiAddress(requestParams, 'CESHI_API_HOST'), {
        routeid: routeid
      }).then(res => {
        if (res.data.code === 200) {
          self.getRouterList()
          self.$message({message: '操作成功', type: 'success'})
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'success'})
        }
      }).catch((err) => {
        self.$message({ message: this.$helper.handleLoginErrorMsg(err), type: 'error' })
      })
    },
    composeValue: function (item, row) {
      return {
        'opearTag': item,
        'row': row
      }
    },
    handleClickAddRoute: function () {
      this.$router.push({path: '/route-create'})
    },
    handleCurrentChange: function (pageIndex) {
      var self = this
      self.currentPageIndex = pageIndex
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/route-manage-list.page.less";
</style>
