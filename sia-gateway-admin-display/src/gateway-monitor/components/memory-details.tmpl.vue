<template>
  <div class="memory-details" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>内存详情</span>
          <i class="close-icon" @click="showHiddenMemoryDetails"></i>
        </div>
        <div class="info more">
          <p>
            <span>内存 (memory)</span>
            <span>
              <i>{{viewDetailObj['mem.free'] | getfilesize}} / {{viewDetailObj.mem | getfilesize}}</i>
              <i>{{getPercent(viewDetailObj['mem.free'], viewDetailObj['mem'])}}</i>
            </span>
          </p>
          <p>
            <span>堆内存 (heap)</span>
            <span>
              <i>{{viewDetailObj['heap.used'] | getfilesize}} / {{viewDetailObj['heap'] | getfilesize}}</i>
              <i>{{getPercent(viewDetailObj['heap.used'], viewDetailObj['heap'])}}</i>
            </span>
          </p>
          <p>
            <span>初始堆 (heap.init)</span>
            <span>{{viewDetailObj['heap.init'] | getfilesize}}</span>
          </p>
          <p>
            <span>最大堆 (heap.committed)</span>
            <span>{{viewDetailObj['heap.committed']}}</span>
          </p>
          <p>
            <span>非堆 (nonheap)</span>
            <span>
              <i>
                {{viewDetailObj['nonheap.used'] | getfilesize}} / {{viewDetailObj['nonheap'] | getfilesize}}
              </i>
              <i>
                {{viewDetailObj['threads.totalStarted'] | getfilesize}}
              </i>
            </span>
          </p>
          <p>
            <span>初始非堆 (nonheap.init)</span>
            <span>{{viewDetailObj['nonheap.init'] | getfilesize}}</span>
          </p>
          <p>
            <span>最大非堆 (nonheap.committed)</span>
            <span>{{viewDetailObj['nonheap.committed'] | getfilesize}}</span>
          </p>
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'MemoryTmpl',
  props: ['memoryDetailsParams'],
  data () {
    return {
      viewDetailObj: {}
    }
  },
  filters: {
    getfilesize (size) {
      if (!size && size !== 0)
        return '';
      var num = 1024.00; // byte
      if (size < num)
        return size
      if (size < Math.pow(num, 2))
        return (size / num).toFixed(2) + 'M'; // M
      if (size < Math.pow(num, 3))
        return (size / Math.pow(num, 2)).toFixed(2) + 'G'; // M
      if (size < Math.pow(num, 4))
        return (size / Math.pow(num, 3)).toFixed(2) + 'T'; // G
      // return (size / Math.pow(num, 4)).toFixed(2) + 'T'; // T
    }
  },
  created () {
    this.getTaskList()
  },
  methods: {
    getPercent (use, total) {
      return ((use / total) * 100).toFixed(2) + '%'
    },
    getTaskList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/monitor/jvm', 'CESHI_API_HOST'), {
        ipport: this.memoryDetailsParams
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
    showHiddenMemoryDetails: function () {
      this.$emit('showHiddenMemoryDetails', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/memory-details.tmpl.less';
</style>
