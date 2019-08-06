<template>
  <div class="dispatch-system-default connecte-test-page">
    <div class="section-container">
      <div class="section-header">
        <span class="title">路由连通性测试</span>
      </div>
      <div class="section-content">
        <div class="test-info">
          <el-input placeholder="请输入测试路由" v-model="connectTestViewModel.requestAddress" class="input-with-select">
            <el-select v-model="connectTestViewModel.requestMethod" slot="prepend" placeholder="请选择">
              <el-option label="GET" value="GET"></el-option>
              <el-option label="POST" value="POST"></el-option>
            </el-select>
            <el-button slot="append" @click="testRequest">测试</el-button>
          </el-input>
          <el-input
            type="textarea"
            class="params-info-text scroll-bar"
            placeholder="请输入测试路由参数"
            v-if="connectTestViewModel.requestMethod === 'POST'"
            v-model="paramsInfoText">
          </el-input>
        </div>
        <div class="test-resolve" :class="{'active': connectTestViewModel.requestMethod === 'POST'}">
          <h2>respones</h2>
          <div class="respones-info">
            <textarea name="" :disabled="true" class="respones-info-text scroll-bar" cols="30" rows="10" v-model="responseInfoFormatJson"></textarea>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConnecteTestPage',
  data () {
    return {
      connectTestViewModel: {
        requestMethod: 'GET',
        requestAddress: ''
      },
      responseInfoFormatJson: '',
      paramsInfoText: ''
    }
  },
  // watch: {
  //   'connectTestViewModel.requestMethod': function (newValue) {
  //     console.log(newValue, '--------------------newValue')
  //   }
  // },
  methods: {
    testRequest: function () {
      let self = this
      let paramsObj = {
        'url': self.connectTestViewModel.requestAddress,
        'method': self.connectTestViewModel.requestMethod
      }
      if (self.connectTestViewModel.requestAddress === '') {
        self.$message({ message: '请输入测试路由！', type: 'error' })
        return false
      }
      if (this.connectTestViewModel.requestMethod === 'POST') {
        if (this.paramsInfoText === '') {
          self.$message({ message: '请输入测试路由参数！', type: 'error' })
          return false
        }
        paramsObj.body = this.paramsInfoText
      }
      self.$http.post(self.$api.getApiAddress('/routetest', 'CESHI_API_HOST'), paramsObj).then(res => {
        if (res.data.code === 200) {
          let data = Object.keys(res).length === 0 ? '' : res
          this.formatJson(JSON.stringify(data))
        } else {
          self.$message({ message: this.$helper.handleLoginErrorMsg(res), type: 'error' })
        }
      }).catch((err) => {
        self.$message({ message: this.$helper.handleLoginErrorMsg(err), type: 'error' })
      })
    },
    formatJson: function (params) {
      this.responseInfoFormatJson = ''
      let k = 0
      let j = 0
      let ii = null
      let ele = null
      for (let i = 0; i < params.length; i++) {
        ele = params.charAt(i)
        if (j % 2 === 0 && ele === '}') {
          k--
          for (ii = 0; ii < k; ii++) {
            ele = '    ' + ele
          }
          ele = '\n' + ele
        } else if (j % 2 === 0 && ele === '{') {
          ele += '\n'
          k++
          for (ii = 0; ii < k; ii++) {
            ele += '    '
          }
        } else if (j % 2 === 0 && ele === ',') {
          ele += '\n'
          for (ii = 0; ii < k; ii++) {
            ele += '    '
          }
        } else if (ele === '"') {
          j++
        }
        this.responseInfoFormatJson += ele
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "../styles/connecte-test.page.less";
</style>
<style lang="less">
@import "../styles/connecte-test.page.reset.less";
</style>
