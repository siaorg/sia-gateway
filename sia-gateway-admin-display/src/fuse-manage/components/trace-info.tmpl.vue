<template>
  <div class="trace-info" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>stackTrace详情</span>
          <i class="close-icon" @click="showHiddenTraceInfo"></i>
        </div>
        <div class="info scroll-bar">
          {{traceInfoParams}}
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditTraceTmpl',
  props: ['traceInfoParams'],
  data () {
    return {
    }
  },
  methods: {
    showHiddenTraceInfo: function () {
      this.$emit('showHiddenTraceInfo', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/trace-info.tmpl.less';
</style>
