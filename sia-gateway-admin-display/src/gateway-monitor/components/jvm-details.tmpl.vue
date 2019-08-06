<template>
  <div class="jvm-details" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>JVM详情</span>
          <i class="close-icon" @click="showHiddenJvmDetails"></i>
        </div>
        <div class="info scroll-bar">
          <p>
            <span>运行时间 (uptime)</span>
            <span>{{$formatDate.dateFormat(viewDetailObj.uptime).split(' ')[0].split('-')[2] + ':' + $formatDate.dateFormat(viewDetailObj.uptime).split(' ')[1]}} [d:h:m:s]</span>
            <!--<span>{{viewDetailObj.uptime}} [d:h:m:s]</span>-->
          </p>
          <p>
            <span>负载 (systemload)</span>
            <span>{{viewDetailObj['systemload.average']}} (last min)</span>
          </p>
          <p>
            <span>核心数 (processors)</span>
            <span>{{viewDetailObj.processors}}</span>
          </p>
          <p>
            <span>类加载 (classes)</span>
            <span>
              <i>
                <em>当前加载类 (loaded)</em>
                <em>{{viewDetailObj['classes']}}</em>
              </i>
              <i>
                <em>已加载类 (total loaded)</em>
                <em>{{viewDetailObj['classes.loaded']}}</em>
              </i>
              <i>
                <em>已卸载类 (unloaded)</em>
                <em>{{viewDetailObj['classes.unloaded']}}</em>
              </i>
            </span>
          </p>
          <p>
            <span>线程 (threads)</span>
            <span>
              <i>
                <em>当前线程 (current)</em>
                <em>{{viewDetailObj['threads']}}</em>
              </i>
              <i>
                <em>已建线程 (totalStarted)</em>
                <em>{{viewDetailObj['threads.totalStarted']}}</em>
              </i>
              <i>
                <em>守护线程 (daemon)</em>
                <em>{{viewDetailObj['threads.daemon']}}</em>
              </i>
              <i>
                <em>最大线程 (peak)</em>
                <em>{{viewDetailObj['threads.peak']}}</em>
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
  props: ['jvmDetailsParams'],
  data () {
    return {
      viewDetailObj: {
        // 'mem': 259413,
        // 'mem.free': 37928,
        // 'heap': 253440,
        // 'heap.init': 65536,
        // 'heap.used': 108547,
        // 'heap.committed': 'heap.committed',
        // 'nonheap': 0,
        // 'nonheap.init': 2496,
        // 'nonheap.used': 112937,
        // 'nonheap.committed': 115776,
        // 'uptime': 8247095,
        // 'processors': 4,
        // 'systemload.average': 0.03,
        // 'classes': 13695,
        // 'classes.loaded': 13695,
        // 'classes.unloaded': 0,
        // 'threads': 51,
        // 'threads.daemon': 47,
        // 'threads.peak': 51,
        // 'threads.totalStarted': 58,
        // 'gc.parnew.count': 2300,
        // 'gc.parnew.time': 6478,
        // 'gc.concurrentmarksweep.count': 21,
        // 'gc.concurrentmarksweep.time': 566
      }
    }
  },
  created () {
    this.getTaskList()
  },
  methods: {
    getTaskList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/monitor/jvm', 'CESHI_API_HOST'), {
        ipport: this.jvmDetailsParams
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
    showHiddenJvmDetails: function () {
      this.$emit('showHiddenJvmDetails', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/jvm-details.tmpl.less';
</style>
