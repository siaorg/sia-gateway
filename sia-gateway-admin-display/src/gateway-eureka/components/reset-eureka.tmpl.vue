<template>
  <div class="mask-reset-eureka" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>重置Eureka</span>
          <i class="close-icon" @click="showHiddenResetetEurekaTmpl"></i>
        </div>
        <div class="info">
          <!--<el-form :model="urlsViewModel" :rules="urlsViewModelRules" ref="resetUrlsForm" label-width="120px" class="dispose-Subgroup-form" auto-complete="off">
            <el-form-item label="URLS" prop="urls">
              <el-input type="text" auto-complete="off" placeholder="请输入URLS" min='1' step="1" v-model="urlsViewModel.urls"></el-input>
            </el-form-item>
          </el-form>-->
          <div class="eureka-urls scroll-bar">
            <el-radio-group v-model="urlsCheckedRadio" v-if="JSON.stringify(urlsList) !== '[]'">
              <el-radio v-for="(item, index) in urlsList" :label="item" :key="index">{{item}}</el-radio>
            </el-radio-group>
            <div class="no-data" v-if="JSON.stringify(urlsList) === '[]'">暂无数据</div>
          </div>
          <div class="button" v-if="JSON.stringify(urlsList) !== '[]'">
            <el-button class="btn reset-btn" @click="showHiddenResetetEurekaTmpl">取消</el-button>
            <el-button class="btn create-btn" @click="showHiddenSave">重置</el-button>
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
      // urlsList: ['http://	10.143.143.33:8088/eureka/1', 'http://	10.143.143.33:8088/eureka/2'],
      urlsList: [],
      urlsCheckedRadio: '',
      urlsViewModel: {
        urls: ''
      },
      urlsViewModelRules: {
        urls: [this.$validator.required('请输入URLS'), { validator: checkBlur, trigger: 'blur' }]
      }
    }
  },
  created () {
    // this.getUrlsList()
  },
  methods: {
    getUrlsList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/setEurekaUrls', 'CESHI_API_HOST'), {
        'zuulGroupName': this.$store.state.frame.currentGatewayGroup
      }).then(res => {
        if (res.data.code === 200) {
          self.urlsList = res.data.response
        }
      }).catch((err) => {
        self.$message({ message: '获取URLS失败', type: 'error' })
      })
    },
    showHiddenSave: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/resetEurekaUrls', 'CESHI_API_HOST'), {
        'zuulGroupName': this.$store.state.frame.currentGatewayGroup
      }).then(res => {
        if (res.data.code === 200 && typeof(res.data.response) !== 'string') {
          self.$message({message: res.data.response, type: 'success'})
        } else {
          self.$message({ message: res.data.response, type: 'error' })
        }
        this.$emit('showHiddenResetetEurekaTmpl', false)
      }).catch((err) => {
        console.log(err, '--------------------err')
        self.$message({ message: '重置失败', type: 'error' })
      })
    },
    showHiddenResetetEurekaTmpl: function () {
      this.$emit('showHiddenResetetEurekaTmpl', false)
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/reset-eureka.tmpl.less';
</style>
