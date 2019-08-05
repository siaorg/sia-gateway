<template>
    <div class="dispatch-system-default task-manage-list-page">
      <div class="section-container">
      <div class="section-header">
        <span class="select-item">组件管理</span>
        <!--<i :class="['el-icon-refresh','refresh-icon',{'loading-refresh':loadingRefresh}]" @click="showHiddenRefreshTaskList"></i>-->
        <el-button class="refresh-btn btn-ml-auto" icon="el-icon-refresh" :loading="loadingRefresh" @click="showHiddenRefreshTaskList">{{loadingRefresh?'加载中':'刷新'}}</el-button>
        <el-button class="btn create-btn" @click="getAllBizCompList()">第三方组件</el-button>
        <el-upload
          class="upload-demo"
          :action="$api.getApiAddress('/upload', 'CESHI_API_HOST')"
          name="file"
          v-if="!uploadGroupLoading"
          :data="uploadParams"
          :on-change="uploadChangeAddParams"
          :on-success="handleFileSuccess"
          :on-error="handleFileError"
          :before-upload="beforeAvatarUpload"
          :auto-upload="true"
          :show-file-list="false">
          <el-button class="btn search-btn">组件上传</el-button>
        </el-upload>
        <el-button class="btn" v-if="uploadGroupLoading" :loading="uploadGroupLoading" :disabled="!uploadGroupLoading">组件上传中</el-button>
      </div>
      <div class="section-content" id="section-content">
        <el-tabs v-model="tabActiveName" @tab-click="changeTabsHandleClick">
          <el-tab-pane v-for="(item, index) in viewGroupManageList" :label="item.cname" :name="item.firstLevel" :key="index">
            <div class="frame-breadcrumb" v-if="tabActiveName === item.firstLevel&&item.compList.length!=0">
              <div v-show="isHideLeftRightIcon" class="left-icon" @click="handleClickMoveRight"><img src="../images/left.png" alt=''></div>
              <transition-group class="bread-crumb-main" :class="{'active': !isHideLeftRightIcon}" ref='breadCrumbMainStyle'>
                <div class="bread-crumb-list" id="bread-crumb-list" key="home" ref="breadCrumbListStyle">
                  <div v-for="(itemChild, indexs) in item.compList" :key="indexs" class="grounp-list" :class="{'active': groupManageDetails.compName===itemChild.compName}" @click="switchGrounpList(itemChild)">
                    <div class="title">
                      <h5>{{itemChild.compName}}</h5>
                      <span><i></i></span>
                    </div>
                    <div class="content">
                      <div class="desc">
                        <p>{{groupManageDetails.zuulGroupName === 'ALL' ? '公共组件' : groupManageDetails.zuulGroupName}}</p>
                        <p>{{itemChild.compName.replace('组件', '管理')}}</p>
                      </div>
                      <div class="oper">
                        <span @click="bindRouterHandleClick(itemChild)">路由绑定</span>
                        <span @click="deleteRouterHandleClick(item, itemChild)">删除</span>
                      </div>
                    </div>
                  </div>
                </div>
              </transition-group>
              <div v-show="isHideLeftRightIcon" class="right-icon" @click="handleClickMoveLeft"><img src="../images/right.png" alt=''></div>
            </div>
            <div class="no-data" v-if="tabActiveName === item.firstLevel&&item.compList.length===0">暂无数据</div>
          </el-tab-pane>
        </el-tabs>
        <div class="details-msg" v-if="JSON.stringify(groupManageDetails)!=='{}'">
          <h2><span></span><span>基本信息</span></h2>
          <ul class="list">
            <li>
              <span>
                <i>组件类别：</i>
                <i>{{groupManageDetails.zuulGroupName === 'ALL' ? '公共组件' : groupManageDetails.zuulGroupName}}</i>
              </span>
              <span>
                <i>执行位置：</i>
                <i>{{groupManageDetails.compType | filterCmpType}}</i>
              </span>
              <span>
                <i>优先级：</i>
                <i>{{groupManageDetails.compOrder}}</i>
              </span>
            </li>
            <li>
              <span>
                <i>状态：</i>
                <i>发布</i>
              </span>
              <span>
                <i>更新时间：</i>
                <i>{{$formatDate.dateFormat(groupManageDetails.compUpdateTime)}}</i>
              </span>
              <span>
              </span>
            </li>
            <li :class="{'bind':groupManageDetails.context!=='' && groupManageDetails.context !== null && groupManageDetails.context.length!==0}">
              <span>
                <i>路由绑定：</i>
                <i v-if="groupManageDetails.context!=='' && groupManageDetails.context !== null">
                  <em v-for="(item,index) in groupManageDetails.context" :key="index" :label="item" v-if="item !== ''">{{item}}<i class="el-icon-close" @click="unbindRouter(groupManageDetails, item)"></i></em>
                </i>
              </span>
            </li>
            <li>
              <span class="desc">
                <i class="desc">描述：</i>
                <i class="desc">{{groupManageDetails.compdesc}}</i>
              </span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <bind-group-list-tmpl  v-if="bindGroupListShow" :bindGroupListParams="bindGroupListParams" v-on:showHiddenBindGroup="showHiddenBindGroup"></bind-group-list-tmpl>
    <third-party-group-tmpl  v-if="thirdPartyGroupShow" :thirdPartyGroupParams="thirdPartyGroupParams" v-on:showHiddenThirdPartyGroup="showHiddenThirdPartyGroup"></third-party-group-tmpl>
    </div>
</template>

<script>
const bindGroupListTmpl = resolve => require(['../components/bind-group-list.tmpl'], resolve)
const thirdPartyGroupTmpl = resolve => require(['../components/third-party-group.tmpl'], resolve)
export default {
  name: 'GroupManageListPage',
  components: {bindGroupListTmpl, thirdPartyGroupTmpl},
  data () {
    return {
      radio: true,
      // tab切换当前选中值
      tabActiveName: 'log',
      loadingRefresh: false,
      uploadGroupLoading: false,
      bindGroupListShow: false,
      thirdPartyGroupShow: false,
      bindGroupListParams: '',
      routeName: '',
      configPath: '',
      serverUrl: '',
      uploadParams: {},
      routerOpeartMoreList: [
        {
          opearTag: '查看组件',
          disable: false
        },
        {
          opearTag: '路由绑定',
          disable: false
        },
        {
          opearTag: '删除',
          disable: false
        }
      ],
      groupManageDetails: {},
      viewGroupManageList: [],
      // 个数是否达到显示左右按钮
      isHideLeftRightIcon: false
    }
  },
  filters: {
    filterStatus (val) {
      switch (val) {
        case 'ok':
          return '发布'
        case 'nook':
          return '未发布'
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
    this.uploadParams = {
      currentRoleName: this.$store.state.frame.currentGatewayGroup ? this.$store.state.frame.currentGatewayGroup.split('-')[0] : ''
    }
    this.getGroupManageList()
  },
  mounted () {
    let self = this
    // 页面发生变化计算屏幕宽度，判断是否显示左右按钮
    window.onresize = () => {
      self.changeTabsHandleClick(self.tabActiveName)
    }
  },
  methods: {
    // 组件切换查看详情
    switchGrounpList (params) {
      this.groupManageDetails = params
    },
    // tab切换
    changeTabsHandleClick (tab) {
      let name = tab.name ? tab.name : tab
      this.viewGroupManageList.forEach((ele) => {
        if (ele.firstLevel === name) {
          if (JSON.stringify(ele.compList) !== '[]') {
            this.groupManageDetails = ele.compList[0]
            let len = ele.compList.length
            let offsetWidth = document.getElementById('section-content').offsetWidth - 140
            // 判断list所占宽度，来确定是否显示左右按钮
            if ((len * 260 + (len - 1) * 15) < offsetWidth) {
              this.isHideLeftRightIcon = false
            } else {
              this.isHideLeftRightIcon = true
            }
          } else {
            this.groupManageDetails = {}
          }
        }
      })
    },
    // 获取元素属性值
    getStyle (obj, attr) {
      return obj.currentStyle ? obj.currentStyle[attr] : getComputedStyle(obj)[attr]
    },
    // 点击做按钮动画
    handleClickMoveRight: function () {
      let breadCrumbListStyle = parseInt(this.getStyle(this.$refs.breadCrumbListStyle[0], 'width'))
      let breadCrumbMainStyle = parseInt(this.getStyle(this.$refs.breadCrumbMainStyle[0].$el, 'width'))
      if (breadCrumbListStyle <= breadCrumbMainStyle && parseInt(this.getStyle(this.$refs.breadCrumbListStyle[0], 'left')) > 0) {
        return
      }
      let dis = parseInt(this.getStyle(this.$refs.breadCrumbListStyle[0], 'left')) + 200
      if (dis > 0) {
        dis = 0
      }
      this.$refs.breadCrumbListStyle[0].style.left = dis + 'px'
    },
    // 点击有按钮动画
    handleClickMoveLeft: function () {
      let breadCrumbListStyle = parseInt(this.getStyle(this.$refs.breadCrumbListStyle[0], 'width'))
      let breadCrumbMainStyle = parseInt(this.getStyle(this.$refs.breadCrumbMainStyle[0].$el, 'width'))
      if (breadCrumbListStyle <= breadCrumbMainStyle) {
        return
      }
      let dis = parseInt(this.getStyle(this.$refs.breadCrumbListStyle[0], 'left')) - 200
      let minWidth = breadCrumbListStyle - breadCrumbMainStyle
      if (dis <= -minWidth) {
        dis = -minWidth
      }
      this.$refs.breadCrumbListStyle[0].style.left = dis + 'px'
    },
    // 刷新按钮
    showHiddenRefreshTaskList: function () {
      let self = this
      self.loadingRefresh = true
      self.getGroupManageList()
      setTimeout(function () {
        self.loadingRefresh = false
      }, 2000)
    },
    // 第三方组件详情隐藏事件
    showHiddenThirdPartyGroup: function (val) {
      this.thirdPartyGroupShow = val
    },
    // 第三方组件详情
    getAllBizCompList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getAllBizCompList', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          if (res.data.response.length !== 0) {
            this.thirdPartyGroupShow = true
            this.thirdPartyGroupParams = res.data.response
          } else {
            self.$message({message: '未上传第三方组件！', type: 'error'})
          }
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    // 组件绑定隐藏事件
    showHiddenBindGroup: function (val, obj) {
      this.bindGroupListShow = val
      this.getGroupManageList(obj)
    },
    // 组件上传前显示上传动画
    beforeAvatarUpload: function () {
      this.uploadGroupLoading = true
    },
    // 选取文件变化时改变参数值
    uploadChangeAddParams: function () {
      this.uploadParams = {
        currentRoleName: this.$store.state.frame.currentGatewayGroup ? this.$store.state.frame.currentGatewayGroup.split('-')[0] : ''
      }
    },
    // 上传成功时，定时调用组件列表，当列表长度增加1渲染页面并时停止定时器
    handleFileSuccess: function (res, file) {
      let self = this
      if (res.code === 200) {
        self.getGroupManageList()
        this.$message({message: '上传成功!', type: 'success'})
      } else {
        this.$message({message: '上传失败!', type: 'error'})
      }
      this.uploadGroupLoading = false
    },
    // 组件上传失败
    handleFileError: function (res, file) {
      this.uploadGroupLoading = false
      this.$message({message: '上传失败!', type: 'error'})
    },
    // 解除组件绑定
    unbindRouter: function (row, tag) {
      let self = this
      let unBindRouteIndex = row.context.indexOf(tag)
      self.$confirm('你确定要将' + tag + '解除绑定么？', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        row.context.splice(unBindRouteIndex, 1)
        self.$http.post(self.$api.getApiAddress('/bindRoute?compFilterName=' + row.compFilterName, 'CESHI_API_HOST'), row.context).then((res) => {
          if (res.data.code === 200) {
            self.$message({message: '路由解绑成功!', type: 'success'})
            this.getGroupManageList(row)
          } else {
            self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
          }
        }).catch(() => {
          self.$message({message: '路由解绑失败!', type: 'error'})
        })
      })
    },
    // 获取组件列表
    getGroupManageList: function (val) {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getCompList', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.viewGroupManageList = res.data.response
          if (val !== undefined) {
            res.data.response.forEach((ele) => {
              ele.compList.forEach((eles) => {
                if (eles.compFilterName === val.compFilterName) {
                  self.groupManageDetails = eles
                }
              })
            })
          } else {
            self.groupManageDetails = self.viewGroupManageList[0].compList[0]
          }
          let len = self.viewGroupManageList[0].compList.length
          let offsetWidth = document.getElementById('section-content').offsetWidth - 140
          if ((len * 260 + (len - 1) * 15) < offsetWidth) {
            this.isHideLeftRightIcon = false
          } else {
            this.isHideLeftRightIcon = true
          }
        } else {
          // self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        // self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    // 组件绑定弹出框
    bindRouterHandleClick: function (params) {
      this.bindGroupListShow = true
      this.bindGroupListParams = params
    },
    // 删除组件
    deleteRouterHandleClick: function (item, params) {
      let self = this
      if (params.zuulGroupName === 'ALL') {
        self.$message({message: '公共组件,不允许删除！', type: 'error'})
      } else if (JSON.stringify(params.context) !== '[]' && JSON.stringify(params.context) !== 'null') {
        self.$message({message: '请先删除绑定路由！', type: 'error'})
      } else {
        self.$confirm('你确定要删除该组件吗？', '', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          self.$http.post(self.$api.getApiAddress('/removeComponent', 'CESHI_API_HOST'), {
            compdesc: params.compdesc,
            compFilterName: params.compFilterName
          }).then((res) => {
            if (res.data.code === 200) {
              if (item.compList.length === 1) {
                self.tabActiveName = 'log'
              }
              this.getGroupManageList()
              self.$message({message: '删除成功！', type: 'success'})
            } else {
              self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
            }
          }).catch((err) => {
            self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
          })
        })
      }
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/group-manage-list.page.less";
</style>
<style lang="less">
@import "../styles/group-manage-list.page.reset.less";
</style>
