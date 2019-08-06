<template>
  <div class="mask-task-list-manage" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{groupListParams}} ~ 组件详情</span>
          <i class="close-icon" @click="showHiddenGroupList"></i>
        </div>
        <div class="info">
          <p class="router-list" v-if="viewDetailObj!==''">
            <span v-for="(item, index) in viewDetailObj" :key="index" v-show="item!==''">{{item}}</span>
          </p>
          <p class="router-list" v-else>
            暂无数据
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'GroupListTmplPage',
  props: ['groupListParams'],
  data () {
    return {
      viewDetailObj: []
    }
  },
  created () {
    this.getRouteGroupList()
  },
  methods: {
    getRouteGroupList: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getRouteCompByRouteID', 'CESHI_API_HOST'), {
        routeid: self.groupListParams
      }).then((res) => {
        this.viewDetailObj = res.data.response !== 'null' ? res.data.response : ''
      })
    },
    showHiddenGroupList: function () {
      this.$emit('showHiddenGroupList', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/group-list.tmpl.less';
</style>
