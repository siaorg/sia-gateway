<template>
  <div class="mask-add-task-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>第三方组件</span>
          <i class="close-icon" @click="showHiddenThirdPartyGroup"></i>
        </div>
        <div class="info">
          <div class="introude">说明：组件ID是组件的唯一标识！</div>
          <ul class="third-group-list">
            <li v-for="(item, index) of thirdPartyGroupParams" :key="index">
              <span><i>组件ID：</i>{{item.compFilterName}}</span>
              <span><i>组件名称：</i>{{item.compName}}</span>
              <span><i>网关组名称：</i>{{item.zuulGroupName}}</span>
            </li>
          </ul>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'AddJobTmpl',
  props: ['thirdPartyGroupParams'],
  methods: {
    showHiddenThirdPartyGroup: function () {
      this.$emit('showHiddenThirdPartyGroup', false)
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/third-party-group.tmpl.less';
</style>
