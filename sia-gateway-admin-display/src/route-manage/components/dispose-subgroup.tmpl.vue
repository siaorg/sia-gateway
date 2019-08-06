<template>
  <div class="mask-dispose-subgroup" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{disposeSubgroupParams.routeid}} ~ 蓝绿部署组件</span>
          <i class="close-icon" @click="showHiddenDisposeSubgroup"></i>
        </div>
        <div class="info">
          <el-form :model="disposeSubgroupViewModel" :rules="disposeSubgroupViewModelRules" ref="disposeSubgroupForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="版本号" prop="version">
              <el-select v-model="disposeSubgroupViewModel.version" placeholder="版本号">
                <el-option v-for="(item,index) in versionList" :key="index" :label="item" :value="item">{{item}}</el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenDisposeSubgroup">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">切换</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'DisposeSubgroupTmpl',
  props: ['disposeSubgroupParams'],
  data () {
    return {
      versionList: [],
      disposeSubgroupViewModel: {
        version: ''
      },
      disposeSubgroupViewModelRules: {
        version: [this.$validator.required('请选择版本号！')]
      }
    }
  },
  created () {
    this.getVersionList()
  },
  methods: {
    getVersionList: function () {
      let self = this
      self.versionList = this.disposeSubgroupParams.resData.allVersions.split(';')
      for (var i = 0; i < self.versionList.length; i++) {
        if (self.versionList[i] === '' || typeof (self.versionList[i]) === 'undefined') {
          self.versionList.splice(i, 1)
          i = i - 1
        }
      }
      self.disposeSubgroupViewModel.version = this.disposeSubgroupParams.resData.currentVersion
    },
    showHiddenSave: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/routeRibbonSave', 'CESHI_API_HOST'), {
        routeid: this.disposeSubgroupParams.routeid,
        serviceId: this.disposeSubgroupParams.serviceId,
        version: self.disposeSubgroupViewModel.version
      }).then(res => {
        self.$message({message: '版本号添加成功', type: 'success'})
        this.$emit('showHiddenDisposeSubgroup', false)
      }).catch(() => {
        self.$message({ message: '版本号添加失败', type: 'error' })
      })
    },
    showHiddenDisposeSubgroup: function () {
      this.$emit('showHiddenDisposeSubgroup', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/dispose-subgroup.tmpl.less';
</style>
