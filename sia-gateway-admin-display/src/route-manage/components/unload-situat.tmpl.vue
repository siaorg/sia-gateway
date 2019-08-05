<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>路由上传情况</span>
          <i class="close-icon" @click="showHiddenRouterUpdate"></i>
        </div>
        <div class="info">
          <p class="router-update success">
            <span class="title">成功路由ID：</span>
            <span v-for="item in routerUpdate.success">{{item}}</span>
          </p>
          <p class="router-update err">
            <span class="title">失败路由ID：</span>
            <span v-for="item in routerUpdate.fail">{{item}}</span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'GroupListTmplPage',
  props: ['routerUpdate'],
  data () {
    return {
    }
  },
  created () {
  },
  methods: {
    showHiddenRouterUpdate: function () {
      this.$emit('showHiddenRouterUpdate', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/group-list.tmpl.less';
</style>
