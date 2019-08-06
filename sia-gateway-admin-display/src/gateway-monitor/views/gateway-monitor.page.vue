<template>
    <div class="dispatch-system-default gateway-monitor-page">
      <div class="section-container">
        <div class="section-content">
          <el-table :data="viewListData" border style="width: 100%" class="task-manage-table">
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulInstanceId" label="服务实例">
            </el-table-column>
            <el-table-column prop="path" label="监控" width="70">
              <template slot-scope="scope">
                <span @click="showMonitorTmpl(scope.row)"><img src="../images/monitor.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="日志" width="70">
              <template slot-scope="scope">
                <span @click="showLogTmpl(scope.row)"><img src="../images/log.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="JVM" width="70">
              <template slot-scope="scope">
                <span @click="showJvmTmpl(scope.row)"><img src="../images/msg.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="内存" width="75">
              <template slot-scope="scope">
                <span @click="showMemoryTmpl(scope.row)"><img src="../images/msg.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="垃圾回收" width="80">
              <template slot-scope="scope">
                <span @click="showGarbageCollectionTmpl(scope.row)"><img src="../images/msg.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="配置" width="70">
              <template slot-scope="scope">
                <span @click="showEventsTmpl(scope.row)"><img src="../images/events.png" alt=""></span>
              </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulStatus" label="状态">
              <template slot-scope="scope">
                <i :class="scope.row.zuulStatus">{{scope.row.zuulStatus | filterStatus}}</i>
              </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulGroupName" label="节点集群名">
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulStatus" label="最后一次重启时间">
              <template slot-scope="scope">
                <i>{{$formatDate.dateFormat(scope.row.zuulLastStartTime)}}</i>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="bg-empty"></div>
        <div class="section-header">
          <h3><span>拓扑图</span></h3>
          <div class="tooltip-box">
            <span>正常请求</span>
            <span>异常请求</span>
            <span>历史请求</span>
 	        </div>
          <div class="jsPlumbBox scroll-bar" id="jsPlumbBox" v-if="showTopoDetails" style="width:100%;"></div>
          <div class="no-data" v-if="!showTopoDetails"></div>
        </div>
      </div>
      <!--Garbage Collection-->
      <garbage-collection-details-tmpl  v-if="garbageCollectionDetailsShow" :garbageCollectionDetailsParams="garbageCollectionDetailsParams" v-on:showHiddenGarbageCollectionDetails="showHiddenGarbageCollectionDetails">
      </garbage-collection-details-tmpl>
      <!--日志-->
      <log-details-tmpl  v-if="logDetailsShow" :logDetailsParams="logDetailsParams" v-on:showHiddenLogDetails="showHiddenLogDetails">
      </log-details-tmpl>
      <!--memory-->
      <memory-details-tmpl  v-if="memoryDetailsShow" :memoryDetailsParams="memoryDetailsParams" v-on:showHiddenMemoryDetails="showHiddenMemoryDetails">
      </memory-details-tmpl>
      <!--JVM信息-->
      <jvm-details-tmpl  v-if="jvmDetailsShow" :jvmDetailsParams="jvmDetailsParams" v-on:showHiddenJvmDetails="showHiddenJvmDetails">
      </jvm-details-tmpl>
      <!--events-->
      <events-details-tmpl  v-if="eventsDetailsShow" :eventsDetailsParams="eventsDetailsParams" v-on:showHiddenEventsDetails="showHiddenEventsDetails">
      </events-details-tmpl>
    </div>
</template>

<script>
const garbageCollectionDetailsTmpl = resolve => require(['../components/garbage-collection-details.tmpl'], resolve)
const logDetailsTmpl = resolve => require(['../components/log-details.tmpl'], resolve)
const memoryDetailsTmpl = resolve => require(['../components/memory-details.tmpl'], resolve)
const jvmDetailsTmpl = resolve => require(['../components/jvm-details.tmpl'], resolve)
const eventsDetailsTmpl = resolve => require(['../components/events-details.tmpl'], resolve)
import $ from 'jquery'
const jsPlumb=require('jsplumb').jsPlumb
export default {
  name: 'GatewayMonitorPage',
  components: {garbageCollectionDetailsTmpl, logDetailsTmpl, memoryDetailsTmpl, eventsDetailsTmpl, jvmDetailsTmpl},
  data () {
    return {
      // Garbage Collection弹出框
      garbageCollectionDetailsShow: false,
      garbageCollectionDetailsParams: {},
      // 日志弹出框
      logDetailsShow: false,
      logDetailsParams: {},
      // 审计弹出框
      memoryDetailsShow: false,
      memoryDetailsParams: {},
      // 终端信息弹出框
      jvmDetailsShow: false,
      jvmDetailsParams: {},
      // events弹出框
      eventsDetailsShow: false,
      eventsDetailsParams: {},
      viewListData: [],
      // 是否显示拓扑图
      showTopoDetails: true,
      topoObj: {
        // "clientNodes": [
        //     "应用:127.0.0.1:8080222222222222222222222222222222222222222",
        //     "浏览器:127.0.0.1:8081",
        //     "127.0.0.1:8082sssssssssssssssssssssssssssssssssssssssssssssss"
        // ],
        // "zuulNodes": [
        //     "10.10.168.19:8080"
        // ],
        // "upstreamNodes": [
        //     "10.10.168.19:8099"
        // ],
        // "link": [
        //     {
        //         "dest": "10.10.168.19:8080",
        //         "source": "应用:127.0.0.1:8080222222222222222222222222222222222222222"
        //     },
        //     {
        //         "dest": "10.10.168.19:8080",
        //         "source": "浏览器:127.0.0.1:8081"
        //     },
        //     {
        //         "dest": "10.10.168.19:8080",
        //         "source": "127.0.0.1:8082sssssssssssssssssssssssssssssssssssssssssssssss"
        //     },
        //     {
        //         "dest": "10.10.168.19:8099",
        //         "source": "10.10.168.19:8080"
        //     }
        // ],
        // "routeId": "sdasd",
        // "groupName": "DEV-GATEWAY-CORE",
        // "maxDelay":"12354",
        // "minDely":"3",
        // "recentMeg":"2109:06:14:13:02"
      },
      //jsPlumbJson数据
      jsPlumbJson: [],
      //jsPlumbBox容器
      jsPlumbBox: "#jsPlumbBox",
      //连接线数组（用于删除连接线）
		  connectArray: new Array(),
      //端点数组（用于删除端点）
      endpointSourceArray: new Array(),
      targetSourceArray: new Array(),
      //存放初始化jsPlumb实例
		  instanceArray: new Array(),
      //记录节点Icon对应的连接端点数组编号
		  iconSourceArray: new Array(),
      //节点Icon模版
      iconTemplate: [
        // "<h5 class=\"{class}\" id=\"{id}\" title=\"{nodeName}\">{nodeName}</h5>",
        '<div class=\'{class}\' id=\'{id}\'>',
        '<h5>{nodeName}</h5>',
        '<p class=\'mark-box\'>',
        '<em></em>',
        '<span><i>实例名称：</i><i>{nodeName}</i></span>',
        '</p>',
        '</div>'
      ].join(""),
      //出发端点配置
    }
  },
  filters: {
    filterStatus: function (val) {
      switch (val) {
        case 'Running':
          return '运行中'
        case 'Dead':
          return '异常'
      }
    }
  },
  created () {
    this.getListData()
  },
  mounted () {
    this.getGantewayTopo()
    // this.get_nodes(this.topoObj)
  },
  methods: {
    // 获取列表信息
    getListData: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/queryZuulListInfo', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.viewListData = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    getGantewayTopo: function (data) {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getGatewayTopo', 'CESHI_API_HOST'), {
        groupName: this.$store.state.frame.currentGatewayGroup
      }).then((res) => {
        if (res.data.code === 200) {
          let data = res.data.response !== null ? res.data.response : []
          if (res.data.response !== null) {
            // this.initTree(data)
            this.topoObj = data
            this.get_nodes(res.data.response)
            this.showTopoDetails = true
          } else {
            this.showTopoDetails = false
          }
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        console.log(err)
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    // 监控显示
    showMonitorTmpl: function (val) {
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.$router.push({path: '/monitor-list', query: { ipport: val.zuulInstanceId }})
      // this.monitorDetailsShow = true
    },
    showGarbageCollectionTmpl: function (val) {
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.garbageCollectionDetailsParams = val.zuulInstanceId
      this.garbageCollectionDetailsShow = true
    },
    showHiddenGarbageCollectionDetails: function (val) {
      this.garbageCollectionDetailsShow = val
    },
    // log显示
    showLogTmpl: function (val) {
      // this.logDetailsShow = true
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.$router.push({path: '/gateway-log-list', query: { ipport: val.zuulInstanceId }})
    },
    showHiddenLogDetails: function (val) {
      this.logDetailsShow = val
    },
    // Memory显示
    showMemoryTmpl: function (val) {
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.memoryDetailsParams = val.zuulInstanceId
      this.memoryDetailsShow = true
    },
    showHiddenMemoryDetails: function (val) {
      this.memoryDetailsShow = val
    },
    // jvm信息显示
    showJvmTmpl: function (val) {
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.jvmDetailsParams = val.zuulInstanceId
      this.jvmDetailsShow = true
    },
    showHiddenJvmDetails: function (val) {
      this.jvmDetailsShow = val
    },
    // events显示
    showEventsTmpl: function (val) {
      if (val.zuulStatus === 'Dead') {
        this.$message({message: '实例异常，无法查看', type: 'error'})
        return false
      }
      this.eventsDetailsParams = val.zuulInstanceId
      this.eventsDetailsShow = true
    },
    showHiddenEventsDetails: function (val) {
      this.eventsDetailsShow = val
    },
    get_nodes (data) {
      let nodeListData = []
      let nodeDataObj = {}
      let boxWidth = document.getElementById('jsPlumbBox').offsetWidth
      let boxHeigth = (document.getElementById('jsPlumbBox').offsetHeight - 50) / 2
      let len = data.clientNodes.length + 1
      let changeNode = {}
      data.clientNodes.forEach((ele, index) => {
        let className = ''
        let name = ''
        if (ele.indexOf('应用') !== -1) {
          className = 'apply'
          name = ele.substring(3)
        } else if (ele.indexOf('浏览器') !== -1) {
          className = 'browser'
          name = ele.substring(4)
        } else {
          className = 'group'
          name = ele
        }
        nodeListData.push({
          'nodeId': 'l' + index,
          'nodeName': name,
          'preRouter': [],
          'left': len > 1 ? (((boxWidth / (len)) * (index + 1)) - 90) : ((boxWidth / 2) - 90),
          'top': 90,
          'className': className,
          'bottom': 'top'
        })
        nodeDataObj[ele] = 'l' + index
      })
      let zlen = data.zuulNodes.length + 1
      data.zuulNodes.forEach((ele, index) => {
        let perNode = []
        let time = ''
        let state = ''
        nodeDataObj[ele] = 'z' + index
        data.link.forEach((elePre, index) => {
          if (elePre.dest === ele) {
            time = elePre.lastRequestTime
            state = elePre.state
            if (perNode.indexOf(elePre.source) === -1) {
              perNode.push(nodeDataObj[elePre.source])
            }
          }
        })
        nodeListData.push({
          'nodeId': 'z' + index,
          'nodeName': ele,
          'preRouter': perNode,
          'left': zlen > 1 ? (((boxWidth / (zlen)) * (index + 1)) - 90) : ((boxWidth / 2) - 90),
          'top': index % 2 === 0 ? (boxHeigth + 40) : (boxHeigth + 55),
          'className': 'router',
          'bottom': 'all'
        })
      })
      let ulen = data.upstreamNodes.length + 1
      data.upstreamNodes.forEach((ele, index) => {
        let perNode = []
        let time = ''
        let state = ''
        nodeDataObj[ele] = 'u' + index
        data.link.forEach((elePre, index) => {
          if (elePre.dest === ele) {
            time = elePre.lastRequestTime
            state = elePre.state
            if (perNode.indexOf(elePre.source) === -1) {
              perNode.push(nodeDataObj[elePre.source])
            }
          }
        })
        nodeListData.push({
          'nodeId': 'u' + index,
          'nodeName': ele,
          'preRouter': perNode,
          'left': ulen > 1 ? (((boxWidth / (ulen)) * (index + 1)) - 90) : ((boxWidth / 2) - 90),
          'top': index % 2 === 0 ? ((boxHeigth * 2) - 35) : (boxHeigth * 2),
          'className': 'group',
          'bottom': 'bottom'
        })
      })
      this.jsPlumbJson = nodeListData
      jsPlumb.ready(() => {
        this.jsPlumbInstances(data, nodeDataObj)
      })
    },
    jsPlumbInstances (data, changeNode) {
      let self = this
      const instance = jsPlumb.getInstance({
        Connector: ['Straight', { curviness: 50 }],
        Endpoint: ['Dot', { radius: 5 }],
        DragOptions: { cursor: 'pointer', zIndex: 5000 },
        EndpointStyle: { radius: 5, fill: '#A9B4DA', stroke: 'transparent' },
        ConnectionOverlays: [//箭头样式配置
            ['Arrow', {
              width: 10,
              length: 10,
              location: 1
            }]
        ],
        Container: 'jsPlumbBox',
      })
      instance.bind("connection", function (info) {
        self.setConnectionLabel(info.connection, "Labeltext")
      })
      instance.batch(() => {
        const arrowCommon = { foldback: 0.7, width: 12 }
        for (const point of self.jsPlumbJson) {
          var chartID = point.nodeId;
          var obj = {}
          obj.class = "jsPlumbIcon " + point.className
          obj.id = chartID
          obj.nodeName = point.nodeName
          $(this.jsPlumbBox).append(this.substitute(this.iconTemplate,obj))
	        $("#"+chartID).css("left",point.left+"px").css("top",point.top+"px")
          let isSourceVal = {
            isSource: true,
            isTarget: true,
            dragAllowedWhenFull: true
          }
          if (point.bottom === 'all') {
            instance.addEndpoint(point.nodeId, {
              uuid: `${point.nodeId}-bottom`,
              anchor: 'Bottom',
              maxConnections: -1,
            }, isSourceVal)
            instance.addEndpoint(point.nodeId, {
              uuid: `${point.nodeId}-top`,
              anchor: 'Top',
              maxConnections: -1,
            }, isSourceVal)
          } else if (point.bottom === 'top') {
            instance.addEndpoint(point.nodeId, {
              uuid: `${point.nodeId}-bottom`,
              anchor: 'Bottom',
              maxConnections: -1,
            }, isSourceVal)
          } else if (point.bottom === 'bottom') {
            instance.addEndpoint(point.nodeId, {
              uuid: `${point.nodeId}-top`,
              anchor: 'Top',
              maxConnections: -1,
            }, isSourceVal)
          }
          // init transition
          for (const i of data.link) {
            const uuid = [`${changeNode[i['source']]}-bottom`,`${changeNode[i['dest']]}-top`]
            let color = ''
            if (i.state === 'GREEN') {
              color = "#0AA194"
            }
            if (i.state === 'RED') {
              color = "#E91E63"
            }
            if (i.state === 'GRAY') {
              color = "#D0CFCF"
            }
            instance.connect({
              uuids: uuid,
            },{
              paintStyle: {stroke: color, strokeWidth: 1},
              connectorStyle: {stroke: color, strokeWidth: 1},
              connectorHoverStyle: {stroke: color, strokeWidth: 5 },
              hoverPaintStyle: { stroke: color, strokeWidth: 3 }
            })
          }
        }
        for (const point of self.jsPlumbJson) {
          instance.draggable(`${point.nodeId}`)
        }
      })
      jsPlumb.fire('jsPlumbDemoLoaded', instance);
    },
    setConnectionLabel(connection, label) {
      let self = this
      connection.bind("mouseover", function(conn) {
        let time = ''
        self.topoObj.link.forEach((ele) => {
          if (ele.source.indexOf('应用') !== -1) {
            ele.source = ele.source.substring(3)
          } else if (ele.source.indexOf('浏览器') !== -1) {
            ele.source = ele.source.substring(4)
          }
          console.log(ele, conn,'rrrrrrrrrrrrrrrrrrrrrrrrr')
          if (ele.source === conn.source.outerText && ele.dest === conn.target.outerText) {
            console.log('rrrrrrrrrrrrrrrrrrrrrrrrr')
            time = ele.lastRequestTime !== 0 ? self.$formatDate.dateFormat(ele.lastRequestTime) : '0'
          }
        })
        conn.addOverlay(["Label", { label: '<p><span>最后请求时间：</span><span>' + time + '</span></p>', location:0.5, id: 'connLabel'} ])
      })
      connection.bind("mouseout", function(conn) {
        conn.removeOverlay('connLabel')
      })
    },
    substitute : function(str,object){
      return str.replace(/\\?\{([^}]+)\}/g, function(match, name){
          if (match.charAt(0) == '\\') return match.slice(1);
          return (object[name] != undefined) ? object[name] : '';
      })
    }
  }
}
</script>
<style lang="less" scoped>
@import "../styles/gateway-monitor.page.less";
</style>
<style lang="less">
@import "../styles/gateway-monitor.page.reset.less";
</style>
