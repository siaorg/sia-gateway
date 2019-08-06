<template>
  <div class="garbage-collection-details" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>垃圾回收</span>
          <i class="close-icon" @click="showHiddenGarbageCollectionDetails"></i>
        </div>
        <div class="info more">
          <p>
            <span>parnew 回收器</span>
            <span>
              <i>
                <em>次数 (count)</em>
                <em>{{viewDetailObj['gc.parnew.count']}}</em>
              </i>
              <i>
                <em>时间 (time)</em>
                <em>{{viewDetailObj['gc.parnew.time']}} ms</em>
              </i>
            </span>
          </p>
          <p>
            <span>CMS 回收器</span>
            <span>
              <i>
                <em>次数 (count)</em>
                <em>{{viewDetailObj['gc.concurrentmarksweep.count']}}</em>
              </i>
              <i>
                <em>时间 (time)</em>
                <em>{{viewDetailObj['gc.concurrentmarksweep.time']}} ms</em>
              </i>
            </span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['garbageCollectionDetailsParams'],
  data () {
    return {
      viewDetailObj: {}
    }
  },
  created () {
    this.getTaskList()
  },
  methods: {
    getTaskList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/monitor/jvm', 'CESHI_API_HOST'), {
        ipport: this.garbageCollectionDetailsParams
      }).then((res) => {
        self.viewDetailObj = res.data
        // if (res.data.code === 200) {
        //   self.viewDetailObj = res.data.response
        // } else {
        //   self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        // }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    showHiddenGarbageCollectionDetails: function () {
      this.$emit('showHiddenGarbageCollectionDetails', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/garbage-collection-details.tmpl.less';
</style>
