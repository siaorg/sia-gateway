<template>
  <div class="mask-dispose-subgroup" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{grayPublicParams.routeid}} ~ 金丝雀组件</span>
          <i class="close-icon" @click="showHiddenGrayPublicParams"></i>
        </div>
        <div class="info">
          <el-form :model="currentLimitGroupViewModel" :rules="currentLimitGroupViewModelRules" ref="currentLimitGroupForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="请求参数Head" prop="context">
              <el-input type="text" auto-complete="off" placeholder="请输入请输入需要提取的请求参数头部关键字" min='1' step="1" v-model="currentLimitGroupViewModel.context"></el-input>
              <!--<el-tooltip class="item" effect="dark" content="" placement="top">
                <span class="el-icon-question instruction-icon"></span>
              </el-tooltip>-->
            </el-form-item>
          </el-form>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenGrayPublicParams">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">添加</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'GrayPublicTmpl',
  props: ['grayPublicParams'],
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        case 'context':
          break
      }
      callback()
    }
    return {
      contextList: [],
      currentLimitGroupViewModel: {
        context: ''
      },
      currentLimitGroupViewModelRules: {
        context: [this.$validator.required('请输入请输入需要提取的请求参数头部关键字'), { validator: checkBlur, trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getcontextList()
  },
  methods: {
    getcontextList: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/routeCanaryRibbonSave', 'CESHI_API_HOST'), {
        routeid: this.grayPublicParams.routeid,
        'serviceId': this.grayPublicParams.serviceId
      }).then(res => {
        if (res.data.code === 200) {
          self.currentLimitGroupViewModel.context = res.data.response.context
        }
      })
    },
    showHiddenSave: function () {
      let self = this
      this.$refs.currentLimitGroupForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('/routeCanaryRibbonSave', 'CESHI_API_HOST'), {
            'routeid': this.grayPublicParams.routeid,
            'serviceId': this.grayPublicParams.serviceId,
            'context': this.currentLimitGroupViewModel.context
          }).then(res => {
            if (res.data.code === 200) {
              self.$message({message: '添加成功', type: 'success'})
            } else {
              self.$message({ message: '添加失败', type: 'error' })
            }
            this.$emit('showHiddenGrayPublicParams', false)
          }).catch(() => {
            self.$message({ message: '添加失败', type: 'error' })
          })
        }
      })
    },
    showHiddenGrayPublicParams: function () {
      this.$emit('showHiddenGrayPublicParams', false)
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/dispose-subgroup.tmpl.less';
</style>
