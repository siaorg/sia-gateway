<template>
  <div class="events-details" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>配置详情</span>
          <i class="close-icon" @click="showHiddenEventsDetails"></i>
        </div>
        <div class="info scroll-bar">
          <div v-for="(item, index) in keyListData" :key="index">
            <div class="introude">{{item}}</div>
            <ul class="third-group-list" v-for="(items, indexs) in Object.keys(viewDetailObj[item])" :key="indexs">
              <li>
                <span>{{items}}：</span>
                <span>{{viewDetailObj[item][items]}}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'EditJobTmpl',
  props: ['eventsDetailsParams'],
  data () {
    return {
      viewDetailObj: {},
      keyListData: []
    }
  },
  created () {
    this.getTaskList()
  },
  methods: {
    getTaskList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/monitor/env', 'CESHI_API_HOST'), {
        ipport: this.eventsDetailsParams
      }).then((res) => {
        self.viewDetailObj = res.data
        self.keyListData = Object.keys(self.viewDetailObj)
        self.keyListData.forEach((ele, index) => {
          if (ele.indexOf('file') !== -1) {
            self.keyListData.unshift(ele)
            self.keyListData.splice(index, 1)
          }
        })
        // if (res.data.code === 200) {
        //   self.viewDetailObj = res.data.response
        // } else {
        //   self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        // }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    showHiddenEventsDetails: function () {
      this.$emit('showHiddenEventsDetails', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/events-details.tmpl.less';
</style>
