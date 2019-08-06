<template>
  <div class="mask-set-eureka" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>设置Eureka</span>
          <i class="close-icon" @click="showHiddenSetEurekaTmpl"></i>
        </div>
        <div class="info">
          <el-form :model="urlsViewModel" :rules="urlsViewModelRules" ref="setUrlsForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="URLS" prop="urls">
              <el-input type="text" auto-complete="off" placeholder="请输入URLS" min='1' step="1" v-model="urlsViewModel.urls"></el-input>
              <el-tooltip class="item" effect="dark" content="多个以逗号隔开" placement="top">
                <span class="el-icon-question instruction-icon"></span>
              </el-tooltip>
            </el-form-item>
          </el-form>
          <div class="button">
            <el-button class="btn reset-btn" @click="showHiddenSetEurekaTmpl">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave" :loading="isDisableSetEureka">{{isDisableSetEureka?'设置中':'设置'}}</el-button>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'GrayPublicTmpl',
  data () {
    var checkBlur = (rule, value, callback) => {
      switch (rule.field) {
        // case 'urls':
        //   if (!new RegExp(/^[1-9]{1}[0-9]{0,}$/).test(value)) {
        //     return callback(new Error('金丝雀数量只能是正整数！'))
        //   }
        //   break
      }
      callback()
    }
    return {
      urlsList: [],
      urlsViewModel: {
        urls: ''
      },
      urlsViewModelRules: {
        urls: [this.$validator.required('请输入URLS'), { validator: checkBlur, trigger: 'blur' }]
      },
      urlsList: [],
      isDisableSetEureka: false
    }
  },
  created () {
    this.getUrlsList()
  },
  methods: {
    getUrlsList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getEurekaUrls', 'CESHI_API_HOST'), {
        'zuulGroupName': this.$store.state.frame.currentGatewayGroup
      }).then(res => {
        if (res.data.code === 200 && JSON.stringify(res.data.response) !== null) {
          self.urlsViewModel.urls = res.data.response.join(',')
        }
      }).catch((err) => {
        self.$message({ message: '获取URLS失败', type: 'error' })
      })
    },
    showHiddenSave: function () {
      let self = this
      this.$refs.setUrlsForm.validate(valid => {
        if (valid) {
          this.isDisableSetEureka = true
          self.$http.post(self.$api.getApiAddress('/setEurekaUrls?urls=' + this.urlsViewModel.urls + '&zuulGroupName=' + this.$store.state.frame.currentGatewayGroup, 'CESHI_API_HOST'), {
          }).then(res => {
            if (res.data.code === 200 && typeof(res.data.response) !== 'string') {
              self.$message({message: res.data.response, type: 'success'})
            } else {
              self.$message({ message: res.data.response, type: 'error' })
            }
            this.isDisableSetEureka = false
            this.$emit('showHiddenSetEurekaTmpl', false)
          }).catch((err) => {
            console.log(err, '--------------------err')
            this.isDisableSetEureka = false
            self.$message({ message: this.$helper.handleLoginErrorMsg(err), type: 'error' })
          })
        }
      })
    },
    showHiddenSetEurekaTmpl: function () {
      this.$emit('showHiddenSetEurekaTmpl', false)
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/set-eureka.tmpl.less';
</style>
