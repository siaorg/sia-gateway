<template>
  <div class="mask-edit-desc" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{zuulGroup.zuulGroupName}} ~ 网关描述修改</span>
          <i class="close-icon" @click="showHiddenEditDescTmpl"></i>
        </div>
        <div class="info">
          <el-form :model="editDescViewModel" :rules="editDescViewModelRules" ref="editDescViewForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="网关描述" prop="zuulDesc">
              <el-input type="text" auto-complete="off" placeholder="请输入网关描述" v-model="editDescViewModel.zuulDesc"></el-input>
            </el-form-item>
          </el-form>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenEditDescTmpl">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">添加</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditDescTmpl',
  props: ['zuulGroup'],
  data () {
    return {
      editDescViewModel: {
        zuulDesc: ''
      },
      editDescViewModelRules: {
        zuulDesc: [this.$validator.required('请输入网关描述!')]
      }
    }
  },
  created () {
    this.editDescViewModel.zuulDesc = this.zuulGroup.zuulDesc
  },
  methods: {
    showHiddenSave: function () {
      let self = this
      let params = {
        zuulGroupName: this.zuulGroup.zuulGroupName,
        zuulGroupDesc: this.editDescViewModel.zuulDesc
      }
      this.$refs.editDescViewForm.validate(valid => {
        if (valid) {
          self.$http.post(self.$api.getApiAddress('updateZuulDescByGroupName', 'CESHI_API_HOST'), params).then((res) => {
            if (res.data.code === 200) {
              self.$message({ message: res.data.response, type: 'success' })
            }
            this.$emit('showHiddenEditDescTmpl', false)
          }).catch((err) => {
            self.$message({ message: '修改失败', type: 'error' })
          })
        }
      })
      
    },
    showHiddenEditDescTmpl: function () {
      this.$emit('showHiddenEditDescTmpl', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/edit-desc.tmpl.less';
</style>
