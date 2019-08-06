<template>
  <div class="mask-router-level" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>{{routerLevelParams.routeid}} ~ 关系图</span>
          <el-popover
            v-if="JSON.stringify(routerLevelList) !== '{}'"
            placement="bottom"
            width="570"
            popper-class="tooltip"
            trigger="click">
            <div class="tooltip-box">
              <p>
                <span>最小延迟</span>
                <span>{{routerLevelList.minDelay}}</span>
              </p>
              <p>
                <span>最大延迟</span>
                <span>{{routerLevelList.maxDelay}}</span>
              </p>
              <p>
                <span>异常响应</span>
                <span v-show="Object.keys(routerLevelList.exceptionCause).length !== 0">
                  <i v-for="(item, index) in Object.keys(routerLevelList.exceptionCause)" :key="index">
                    <em class="wid">{{$formatDate.dateFormat(Number(item))}}</em>
                    <em>{{routerLevelList.exceptionCause[item]}}</em>
                  </i>
                </span>
                <span v-show="Object.keys(routerLevelList.exceptionCause).length === 0">无</span>
              </p>
              <p>
                <span>最近延迟</span>
                <span>
                  <i v-for="(item, index) in routerLevelList.recentDelays.all" :key="index">
                    <em>{{item.key}}</em>
                    <em>{{item.value}}</em>
                  </i>
                </span>
              </p>
            </div>
            <i slot="reference" class="detail-icon"><img src="../images/details.png" alt=""></i>
          </el-popover>
          <span class="close-icon" @click="showHiddenRouterLevel"></span>
        </div>
        <div class="info scroll-bar" id="config-info" v-show="showTopoDetails">
          <div class="tooltip-box">
            <span>正常请求</span>
            <span>异常请求</span>
            <span>历史请求</span>
 	        </div>
          <div class="jsPlumbBox scroll-bar" id="jsPlumbBox" v-if="showTopoDetails" style="width:100%; heigth:100%;">
          </div>
        </div>
        <div class="no-data" v-show="!showTopoDetails">
          暂无数据
        </div>
      </div>
  </div>
</template>

<script>
import $ from 'jquery'
const jsPlumb=require('jsplumb').jsPlumb
export default {
  name: 'RouterLevelTmplPage',
  props: ['routerLevelParams'],
  data () {
    return {
      showTopoDetails: true,
      routerLevelList: {
        // "clientNodes": [
        //     "应用:127.0.0.1:8080",
        //     "应用:127.0.0.1:8081"
        // ], 
        // "zuulNodes": [ 
        //     "10.10.168.19:8080",
        //     "10.10.168.19:8081"
        // ], 
        // "upstreamNodes": [ 
        //     "10.143.135.136:8082" 
        // ], 
        // "link": [
        //       { 
        //           "dest": "10.10.168.19:8080", 
        //           "source": "应用:127.0.0.1:8080", 
        //           "state": "GREEN", 
        //           "lastRequestTime": 1561712559390 
        //       },
        //       { 
        //           "dest": "10.10.168.19:8081", 
        //           "source": "应用:127.0.0.1:8081", 
        //           "state": "GREEN", 
        //           "lastRequestTime": 1561712559390 
        //       },
        //       { 
        //           "dest": "10.10.168.19:8081", 
        //           "source": "应用:127.0.0.1:8080", 
        //           "state": "GRAY", 
        //           "lastRequestTime": 1561712559390 
        //       }, 
        //       { 
        //           "dest": "10.143.135.136:8082", 
        //           "source": "10.10.168.19:8080", 
        //           "state": "GRAY", 
        //           "lastRequestTime": 0 
        //       } 
        //   ], 
        //   "routeId": "mutiRegister", 
        //   "groupName": "DEV-GATEWAY-CORE", 
        //   "maxDelay": 645, 
        //   "minDelay": 18, 
        //   "lastRequestTime": 1561712590521, 
        //   "exceptionCause": {}, 
        //   "recentDelays": [
        //       "/op/test/pppppp:645", 
        //       "/op/test/pppppp:36", 
        //       "/op/test/pppppp:42", 
        //       "/op/test/pppppp:18", 
        //       "/op/test/pppppp:645", 
        //       "/op/test/pppppp:36", 
        //       "/op/test/pppppp:42", 
        //       "/op/test/pppppp:18" 
        //   ]
      },
      //jsPlumbJson数据
      jsPlumbJson: [],
      //jsPlumbBox容器
      jsPlumbBox: "#jsPlumbBox",
      //节点Icon模版
      iconTemplate: [
        '<div class=\'{class}\' id=\'{id}\'>',
        '<h5>{nodeName}</h5>',
        '<p class=\'mark-box\'>',
        '<em></em>',
        '<span><i>实例名称：</i><i>{nodeName}</i></span>',
        '</p>',
        '</div>'
      ].join("")
    }
  },
  mounted () {
    this.getTaskList()
    // this.get_nodes(this.routerLevelList)
  },
  methods: {
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
        // sourceId-targetId
        let time = ''
        self.routerLevelList.link.forEach((ele) => {
          if (ele.source.indexOf('应用') !== -1) {
            ele.source = ele.source.substring(3)
          } else if (ele.source.indexOf('浏览器') !== -1) {
            ele.source = ele.source.substring(4)
          }
          if (ele.source === conn.source.outerText && ele.dest === conn.target.outerText) {
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
    },
    getTaskList: function () {
      let self = this
      self.$http.post(self.$api.getApiAddress('/getRouteTopo', 'CESHI_API_HOST'), {
        groupName: this.routerLevelParams.zuulGroupName,
        routeid: this.routerLevelParams.routeid
      }).then((res) => {
        let data = res.data.response !== null ? res.data.response : []
        if (res.data.response !== null) {
          this.get_nodes(data)
          this.routerLevelList = res.data.response !== null ? res.data.response : {}
          this.showTopoDetails = true
        } else {
          this.showTopoDetails = false
        }
      }).catch((err) => {
        console.log(err, '----------------err')
        self.$message({message: '服务未响应！', type: 'error'})
      })
    },
    showHiddenRouterLevel: function () {
      this.$emit('showHiddenRouterLevel', false)
    }
  }
}
</script>
<style lang="less">
@import '../styles/common/router-level.tmpl.less';
</style>
