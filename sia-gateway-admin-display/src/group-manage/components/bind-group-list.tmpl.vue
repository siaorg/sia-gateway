<template>
  <div class="mask-add-task-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>绑定路由</span>
          <i class="close-icon" @click="showHiddenBindGroup"></i>
        </div>
        <div class="info">
            <el-transfer
              filterable
              :filter-method="filterMethod"
              filter-placeholder="请输入路由ID"
              :titles="['路由列表','绑定的路由']"
              v-model="checkedValue"
              :data="routerListData">
            </el-transfer>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenBindGroup">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">保存</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'AddJobTmpl',
  props: ['bindGroupListParams'],
  data () {
    return {
      routerListData: [],
      checkedValue: [],
      filterMethod (query, item) {
        return item.label.indexOf(query) > -1
      }
    }
  },
  created () {
    this.getRouterList()
  },
  methods: {
    getRouterList: function () {
      let self = this
      if (this.bindGroupListParams.context !== null) {
        self.checkedValue = this.bindGroupListParams.context
      }
      self.$http.get(self.$api.getApiAddress('/getRouteList', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          res.data.response.forEach((val, index) => {
            self.routerListData.push({
              key: val.routeid,
              label: val.routeid,
              disabled: false
            })
          })
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    showHiddenBindGroup: function () {
      this.$emit('showHiddenBindGroup', false, this.bindGroupListParams)
    },
    showHiddenSave: function () {
      let self = this
      // if (this.checkedValue.length === 0) {
      //   this.$message({message: '绑定的路由为空', type: 'error'})
      //   return false
      // }
      self.$http.post(self.$api.getApiAddress('/bindRoute?compFilterName=' + this.bindGroupListParams.compFilterName, 'CESHI_API_HOST'), this.checkedValue).then((res) => {
        if (res.data.code === 200) {
          self.$message({message: '路由绑定成功!', type: 'success'})
          this.$emit('showHiddenBindGroup', false, this.bindGroupListParams)
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch(() => {
        self.$message({message: '路由绑定失败!', type: 'error'})
      })
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/bind-group-list.tmpl.less';
</style>
