<template>
  <div class="login">
    <div class="login-box">
      <div class="title">
        <span class="logo"><img src="../images/logo.svg" alt=""></span>
        <!--<span v-if="isShowTwoCode">请使用公司邮箱密码登录</span>-->
      </div>
      <el-form class="login-form" :model="loginViewModel" id="login-form" :rules="loginViewModelRules" ref="loginViewForm" auto-complete="off">
        <el-form-item prop="userName" label="用户名">
          <el-input type="text" v-model="loginViewModel.userName" auto-complete="off" placeholder="请输入用户名">
          </el-input>
        </el-form-item>
        <el-form-item prop="userRole" label="用户角色">
          <el-input id="password" :disabled="isUserAdmin" type="text" v-model="loginViewModel.userRole" auto-complete="off" placeholder="请输入用户角色"  @keyup.enter.native="login">
          </el-input>
        </el-form-item>
        <div class="is-admin">
          <el-checkbox v-model="isUserAdmin">是否是管理员</el-checkbox>
        </div>
        <el-form-item class="submit-form">
          <el-button class="submit" @click="loginBtn">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
/* eslint-disable */
export default {
  name: 'LoginIndexPage',
  data () {
    return {
      // form model
      loginViewModel: {
        userName: '',
        userRole: ''
      },
      isUserAdmin: false,
      loginViewModelRules: {
        userName: {required: true, message: '请输入用户名', trigger: 'change'},
        userRole: {required: true, message: '请输入用户角色', trigger: 'change'}
      }
    }
  },
  watch: {
    isUserAdmin (newValue) {
      if (newValue) {
        this.loginViewModel.userRole = ' '
      }
    }
  },
  created () {
  },
  methods: {
    loginBtn: function () {
      let self = this
      this.$refs.loginViewForm.validate(valid => {
        if (valid) {
          let params = {
            username: this.loginViewModel.userName,
            roleName: this.loginViewModel.userRole === ' ' ? 'admin' : this.loginViewModel.userRole,
            // isAdmin: this.isUserAdmin
          }
          self.$http.get(self.$api.getApiAddress('/loginSystem', 'CESHI_API_HOST'), params).then((res) => {
            if (res.data.code === 200) {
              sessionStorage.setItem('login', 'show')
              this.$router.push({path: '/desktop'})
            } else {
              self.$message({message: res.data.message, type: 'error'})
            }
          }).catch(() => {
            // self.$message({message: '未登录！', type: 'error'})
          })
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/login.page.less";
</style>
<style lang="less">
@import "../styles/login.page.reset.less";
</style>
