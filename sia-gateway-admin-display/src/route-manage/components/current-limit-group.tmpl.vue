<template>
  <div class="mask-dispose-subgroup" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{currentLimitGroupParams}} ~ 限流组件</span>
          <i class="close-icon" @click="showHiddenCurrentLimitGroup"></i>
        </div>
        <div class="info">
          <el-form :model="currentLimitGroupViewModel" :rules="currentLimitGroupViewModelRules" ref="currentLimitGroupForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="限流数量" prop="version">
              <el-input type="text" auto-complete="off" placeholder="请输入限流数量" min='1' step="1" v-model="currentLimitGroupViewModel.version"></el-input>
              <el-tooltip class="item" effect="dark" content="每秒最大请求数!" placement="top">
                <span class="el-icon-question instruction-icon"></span>
              </el-tooltip>
            </el-form-item>
          </el-form>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenCurrentLimitGroup">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">添加</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'CurrentLimitGroupTmpl',
  props: ['currentLimitGroupParams'],
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'version':
          if (!new RegExp(/^[1-9]{1}[0-9]{0,}$/).test(value)) {
            return callback(new Error('限流数量只能是正整数！'))
          }
          break
      }
      callback()
    }
    return {
      versionList: [],
      currentLimitGroupViewModel: {
        version: ''
      },
      currentLimitGroupViewModelRules: {
        version: [this.$validator.required('请输入限流数量'), { validator: checkBlur, trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getVersionList()
  },
  methods: {
    getVersionList: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/routelimitquery', 'CESHI_API_HOST'), {
        routeid: this.currentLimitGroupParams
      }).then(res => {
        if (res.data.code === 200) {
          self.currentLimitGroupViewModel.version = res.data.response
        }
      })
    },
    showHiddenSave: function () {
      let self = this
      this.$refs.currentLimitGroupForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('/routelimitsave', 'CESHI_API_HOST'), {
            routeid: this.currentLimitGroupParams,
            limit: self.currentLimitGroupViewModel.version
          }).then(res => {
            if (res.data.response === self.currentLimitGroupViewModel.version) {
              self.$message({message: '添加成功', type: 'success'})
            } else if (res.data.response === 'null') {
              self.$message({ message: '添加失败', type: 'error' })
            }
            this.$emit('showHiddenCurrentLimitGroup', false)
          }).catch(() => {
            self.$message({ message: '添加失败', type: 'error' })
          })
        }
      })
    },
    showHiddenCurrentLimitGroup: function () {
      this.$emit('showHiddenCurrentLimitGroup', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/dispose-subgroup.tmpl.less';
</style>
