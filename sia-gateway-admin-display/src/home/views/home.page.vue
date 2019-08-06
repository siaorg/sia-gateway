<template>
  <div class="dispatch-system-default home-page">
    <div class="opera-statistics">
      <div class="statistics-list">
        <span>{{callCount}}</span>
        <span><i>调用次数</i></span>
        <span class="img-box"><i></i></span>
        <span>路由累计调用次数</span>
      </div>
      <div class="statistics-list">
        <span>{{registerNumber}}</span>
        <span><i>路由注册数量</i></span>
        <span class="img-box"><i></i></span>
        <span>系统中路由注册数量</span>
      </div>
      <div class="statistics-list">
        <span>{{warningNumber}}</span>
        <span><i>预警个数</i></span>
        <span class="img-box"><i></i></span>
        <span>网关发生异常数量</span>
      </div>
    </div>
    
    <div class="run-status">
      <!--<h2>运行状况</h2>-->
      <div class="show-run-status">
        <div class="show-list" id="show-list-number"></div>
        <div class="show-list" id="show-list-b"></div>
        <div class="show-list">
          <h3><span></span><span>监控告警</span></h3>
          <div class="scroll-warning">
            <p v-show="warningMonitor.length !== 0" class="add-scroll-style">
              <!--<span><i>告警时间</i><i>告警实例</i><i>查看</i></span>-->
              <span v-for="(item, index) in warningMonitor" :key="index">
                <el-tooltip class="item" effect="dark" :content="$formatDate.dateFormat(item.alarmCreateTime)" placement="top">
                  <i>{{$formatDate.dateFormat(item.alarmCreateTime)}}</i>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" :content="item.zuulInstance" placement="top">
                  <i>{{item.zuulInstance}}</i>
                </el-tooltip>
                <i class="see-info" @click="showMonitorWarningDetail(item)">查看</i>
              </span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="status-table">
      <div class="table-list flexs scroll-bar">
        <h3><span></span><span>网关集群状态</span></h3>
        <div class="gateway-status">
          <el-table :data="viewGatewayStatusList" border class="gateway-status-table">
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulGroupName" label="节点集群名">
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulInstanceId" label="节点编号">
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="zuulStatus" label="状态" width="80">
              <template slot-scope="scope">
                <span :class="scope.row.zuulStatus">{{scope.row.zuulStatus | filterStatus}}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="90">
              <template slot-scope="scope">
                <div class="caozuo">
                  <span @click="handleSelectGatewayOpear('详情', scope.row)">详情</span>
                  <span @click="handleSelectGatewayOpear('路由', scope.row)">路由</span>
                  <span @click="handleSelectGatewayOpear('删除', scope.row)">删除</span>
                </div>
                <!--<el-button class="list-btn see-button-small" @click="showGatewayStatusDetail(scope.row)"> 查看 </el-button> 
                <el-button class="list-btn delete-button-small" :disabled="scope.row.zuulStatus === 'Running'" @click="showMonitorStatusDelete(scope.row, '1')"> 删除 </el-button> -->
                <!--<el-dropdown size="small" split-button type="primary" class="list-select" @command="handleSelectGatewayOpear" @visible-change="handleChangeGatewayOpear(scope.row, $event)">
                  更多
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="(item, index) in gatewayOpeartMoreList" :disabled="item.disable" :key="index" :command="composeValue(item.opearTag, scope.row)">{{item.opearTag}}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>-->
              </template>
            </el-table-column>
          </el-table>
        </div>
        <h3><span></span><span>管理端集群状态</span></h3>
        <div class="gateway-status">
          <el-table :data="viewMonitorStatusList" border class="gateway-status-table">
            <el-table-column show-overflow-tooltip :resizable='false' prop="adminHotsName" label="节点集群名">
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="adminInstanceid" label="节点编号">
            </el-table-column>
            <el-table-column show-overflow-tooltip :resizable='false' prop="adminStatus" label="状态" width="80">
              <template slot-scope="scope">
                <span :class="scope.row.adminStatus">{{scope.row.adminStatus | filterAdminStatus}}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="90">
              <template slot-scope="scope"> 
                <div class="caozuo">
                  <span @click="handleSelectMonitorOpear('查看', scope.row)">详情</span>
                  <span @click="handleSelectMonitorOpear('删除', scope.row)">删除</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="table-list max scroll-bar">
        <h3><span></span><span>网关集群状态</span></h3>
        <div class="echarts-list">
          <div class="chart-display scroll-bar">
            <div class="scroll-bars">
              <div class="echarts-box" id="dashboard-box" style="width:200px;height:200px;"></div>
              <div class="echarts-box" id="pie-chart" style="width:200px;height:200px;"></div>
              <div class="echarts-box" id="line-chart" style="width:200px;height:200px;"></div>
            </div>
          </div>
          <div class="monitor-list">
            <el-radio-group v-model="monitorCheckedRadio">
              <el-radio v-for="(item, index) in viewGatewayStatusList" v-if="item.zuulStatus !== 'Dead'" :label="item.zuulInstanceId" :key="index">{{item.zuulInstanceId}}</el-radio>
            </el-radio-group>
          </div>
        </div>
      </div>
    </div>
    <gateway-status-detail-tmpl  v-if="gatewayStatusDetailShow" :gatewayDetailParams="gatewayDetailParams" v-on:showHiddenGateway="showHiddenGateway"></gateway-status-detail-tmpl>
    <monitor-center-detail-tmpl  v-if="monitorCenterDetailShow" :monitorDetailParams="monitorDetailParams" v-on:showHiddenMonitor="showHiddenMonitor"></monitor-center-detail-tmpl>
    <monitor-Warning-detail-tmpl v-if="monitorWarningDetailShow" :monitorWarningParams="monitorWarningParams" v-on:showHiddenMonitorWarning="showHiddenMonitorWarning"></monitor-Warning-detail-tmpl>
    <gateway-router-detail-tmpl  v-if="gatewayRouterDetailShow" :routerDetailParams="routerDetailParams" v-on:showHiddeRouterDetail="showHiddeRouterDetail"></gateway-router-detail-tmpl>
  </div>
</template>

<script>
const gatewayStatusDetailTmpl = resolve => require(['../components/gateway-status-detail.tmpl'], resolve)
const monitorCenterDetailTmpl = resolve => require(['../components/monitor-center-detail.tpml'], resolve)
const monitorWarningDetailTmpl = resolve => require(['../components/monitor-warning-detail.tpml'], resolve)
const gatewayRouterDetailTmpl = resolve => require(['../components/gateway-route-detail.tmpl'], resolve)
export default {
  name: 'HomePage',
  components: {gatewayStatusDetailTmpl, monitorCenterDetailTmpl, monitorWarningDetailTmpl, gatewayRouterDetailTmpl},
  data () {
    return {
      gatewayStatusDetailShow: false,
      monitorCenterDetailShow: false,
      monitorWarningDetailShow: false,
      gatewayRouterDetailShow: false,
      routerDetailParams: '',
      gatewayDetailParams: {},
      monitorDetailParams: {},
      monitorWarningParams: {},
      callCount: '0',
      registerNumber: '0',
      warningNumber: '0',
      warningMonitor: [],
      gatewayOpeartMoreList: [
        {
          opearTag: '详情查看',
          disable: false
        },
        {
          opearTag: '路由查看',
          disable: false
        },
        {
          opearTag: '删除',
          disable: false
        }
      ],
      viewGatewayStatusList: [],
      viewMonitorStatusList: [],
      roleList: '',
      monitorCheckedRadio: '', // 监控当前选中项
      monitorStatisObj: {
        'uptime': 7221120,
        'processors': 4, // cpu核数
        'systemload.average': 0.11, // cpu负载，最大等于核数
        'mem': 338526,
        'mem.free': 182620,
        'keepAliveCount': -1
      }
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
    },
    filterAdminStatus: function (val) {
      switch (val) {
        case 'running':
          return '运行中'
        case 'dead':
          return '异常'
      }
    }
  },
  watch: {
    'monitorCheckedRadio': function (newValue) {
      this.getMonitorStatus(newValue)
    }
  },
  created () {
    this.getPandectdata()
  },
  mounted () {
    this.getApiHealth()
    this.getInvocationTrend()
    // this.setDashboardEcharts()
    // this.setPieCharts()
    // this.setLineCharts()
  },
  methods: {
    setLineCharts: function (params) {
      let myChart = this.$echarts.init(document.getElementById('line-chart'))
      myChart.setOption({
        color: ['#34BFCA', '#FFB12A'],
        title: {
          text: '连接数:' + params.keepAliveCount,
          subtext: '上限值:' + params.maxConnections, // 子标题
          x: 'center',
          bottom: -5,
          textStyle: {
            fontWeight: 'normal',
            color: '#fff',
            fontSize: '10'
          },
          subtextStyle: {
            fontWeight: 'normal',
            color: 'rgba(255,255,255,0.7)',
            fontSize: '14'
          }
        },
        tooltip : {
          trigger: 'axis',
          axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          left: '5%',
          right: '5%',
          top: '15%',
          bottom: '25%',
          containLabel: true
        },
        xAxis : [
          {
            type : 'category',
            data : ['ConnectCOUNT'],
            axisLine: {
              lineStyle: {
                color: '#54556E'
              }
            },
            axisLabel: {
              show: false
            }
          }
        ],
        yAxis : [
          {
            type: 'value',
            splitLine: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#54556E'
              }
            },
            axisLabel: {
              show: false
            }
          }
        ],
        series: [
          {
            name: '连接数',
            type: 'bar',
            barWidth: '60%',
            data: [params.keepAliveCount],
            itemStyle: {
              normal: {
                color: '#34BFCA'
              }
            },
            barWidth: 25 // 柱图宽度
          },
          // {
          //   name: '直接访问0',
          //   type: 'bar',
          //   barWidth: '60%',
          //   data: [8],
          //   itemStyle: {
          //     normal: {
          //       color: '#FFB12A'
          //     }
          //   },
          //   barWidth: 25 // 柱图宽度
          // }
        ]
      })
    },
    getfilesize(size) {
      if (!size)
        return '';
      var num = 1024.00; // byte
      if (size < num)
        return size + 'B';
      if (size < Math.pow(num, 2))
        return (size / num).toFixed(2) + 'M'; // kb
      if (size < Math.pow(num, 3))
        return (size / Math.pow(num, 2)).toFixed(2) + 'G'; // M
      if (size < Math.pow(num, 4))
        return (size / Math.pow(num, 3)).toFixed(2) + 'T'; // G
      // return (size / Math.pow(num, 4)).toFixed(2) + 'T'; // T
    },
    setPieCharts: function (params) {
      let myChart = this.$echarts.init(document.getElementById('pie-chart'))
      let memfree = this.getfilesize(params['mem.free'])
      let mem = this.getfilesize(params['mem'])
      let memFreeValue = parseFloat(mem) - parseFloat(memfree)
      myChart.setOption({
        title: {
          text: 'JVM已用内存:' + memfree,
          subtext: 'JVM总内存:' + mem, // 子标题
          x: 'center',
          bottom: -5,
          textStyle: {
            fontWeight: 'normal',
            color: '#fff',
            fontSize: '10'
          },
          subtextStyle: {
            fontWeight: 'normal',
            color: 'rgba(255,255,255,0.7)',
            fontSize: '14'
          }
        },
        color: ['#60BECA', '#54556E'],
        tooltip : {
          trigger: 'item',
          formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        series : [
          {
            name: '内存',
            type: 'pie',
            radius : '70%',
            center: ['50%', '46%'],
            label: {
              normal: {
                show: false
              }
            },
            data:[
              {value: parseFloat(memfree), name:'JVM已用内存'},
              {value: memFreeValue.toFixed(2), name:'JVM剩余内存'},
            ],
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    },
    setDashboardEcharts: function (params) {
      let myChart = this.$echarts.init(document.getElementById('dashboard-box'))
      myChart.setOption({
        title: {
          text: 'CPU负载:' + (params['systemload.average'] * 100).toFixed(2) + '%',
          subtext: 'CPU核心:' + params['processors'], // 子标题
          x: 'center',
          bottom: -5,
          textStyle: {
            fontWeight: 'normal',
            color: '#fff',
            fontSize: '10'
          },
          subtextStyle: {
            fontWeight: 'normal',
            color: 'rgba(255,255,255,0.7)',
            fontSize: '14'
          }
        },
        tooltip: {
          formatter: "{a} <br/>{b} : {c}%"
        },
        series: [
          {
            name: 'CPU',
            type: 'gauge',
            max: params['processors'] * 100, // 最大刻度--------
            splitNumber: 10, // 刻度数量
            // radius: '80%',
            detail: {formatter:'{value}%'},
            title : { // tilte隐藏
              show: false
            },
            splitLine: { // 分隔线
              length: 15, // 属性length控制线长
              lineStyle: { // 属性lineStyle控制线条样式
                color: 'auto'
              }
            },
            pointer: { // 指针
              width:2,
              shadowColor : '#fff', // 默认透明
              shadowBlur: 5
            },
            axisTick: { // 坐标轴小标记
              show: false,
              length :10, // 属性length控制线长
              lineStyle: { // 属性lineStyle控制线条样式
                color: 'auto',
                shadowColor : '#fff', //默认透明
                shadowBlur: 10
              }
            },
            detail: {
              show: false,
              // 数据详情显示
              fontSize: '12',
              textStyle: { // 其余属性默认使用全局文本样式
                color: '#fff'
              },
              offsetCenter:[0, '90%'],
              formatter: [
                '已用{value} ',
                '总数：' + name
              ].join('\n')
            },
            axisLabel: {
              color: 'auto',
              distance: 1
            },
            axisLine: { // 坐标轴线
              lineStyle: { // 属性lineStyle控制线条样式
                color: [[0.5, '#3DBDCB'],[0.8, '#FFB12A'],[1, '#FF7281']],
                width: 6,
                // shadowColor : '#fff', //默认透明
                // shadowBlur: 10
              }
            },
            data: [{name: '使用率', value: (params['systemload.average'] * 100).toFixed(2)}] // 使用---------
          }
        ]
      })
    },
    getMonitorStatus: function (params) {
      let self = this
      self.$http.get(self.$api.getApiAddress('/monitor/simple', 'CESHI_API_HOST'), {
        ipports: params
      }).then((res) => {
        this.setDashboardEcharts(res.data[params])
        this.setPieCharts(res.data[params])
        this.setLineCharts(res.data[params])
        // if (res.data.code === 200) {
        //   // this.setDashboardEcharts(res.data.response)
        //   // this.setPieCharts(res.data.response)
        //   // this.setLineCharts(res.data.response)
        // } else {
        //   // self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        // }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    composeValue: function (item, row) {
      return {
        'opearTag': item,
        'row': row
      }
    },
    handleChangeGatewayOpear: function (row) {
      switch (row.zuulStatus) {
        case 'Running':
          this.gatewayOpeartMoreList.forEach(function (element) {
            if (element.opearTag === '删除') {
              element.disable = true
            } else {
              element.disable = false
            }
          })
          break
      }
    },
    handleSelectGatewayOpear: function (params, row) {
      let self = this
      if (params === '详情') {
        this.gatewayDetailParams = row
        this.gatewayStatusDetailShow = true
      } else if (params === '路由') {
        this.gatewayRouterDetailShow = true
        this.routerDetailParams = row.zuulInstanceId
      } else {
        if (row.zuulStatus !== 'Running') {
          self.showMonitorStatusDelete(row, '1')
        } else {
          self.$message({message: '运行状态不可删除！', type: 'error'})
        }
      }
    },
    handleSelectMonitorOpear: function (params, row) {
      let self = this
      if (params === '查看') {
        this.monitorDetailParams = row
        this.monitorCenterDetailShow = true
      } else {
        if (row.adminStatus !== 'running') {
          self.showMonitorStatusDelete(row, '2')
        } else {
          self.$message({message: '运行状态不可删除！', type: 'error'})
        }
      }
    },
    showHiddeRouterDetail: function (val) {
      this.gatewayRouterDetailShow = val
    },
    showMonitorStatusDelete: function (row, type) {
      let self = this
      let modalParams = {}
      let requestPath = ''
      if (type === '1') {
        modalParams = {zuulInstanceId: row.zuulInstanceId}
        requestPath = '/deleteZuul'
      } else {
        modalParams = {adminInstanceid: row.adminInstanceid}
        requestPath = '/deletAdmin'
      }
      self.$confirm('你确定要删除' + (type === '1' ? row.zuulGroupName : row.adminHotsName) + '吗?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        self.$http.post(self.$api.getApiAddress(requestPath, 'CESHI_API_HOST'), modalParams).then((res) => {
          if (res.data.code === 200) {
            self.$message({message: '删除成功！', type: 'success'})
            if (type === '1') {
              self.getGatewayStatusList()
            } else {
              self.getMonitorStatusList()
            }
          }
        }).catch(() => {
          self.$message({message: '删除失败！', type: 'error'})
        })
      })
    },
    getInvocationTrend: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getAPICountArray', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.invocationTrend(res.data.response)
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    getApiHealth: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getAPIHealthyArray', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.apiHealth(res.data.response)
        } else {
          self.$message({message: this.$helper.handleLoginErrorMsg(res), type: 'error'})
        }
      }).catch((err) => {
        console.log(err)
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    apiHealth: function (params) {
      let colorList = ['#FFB029', '#946CFF', '#F26186', '#E91E63', '#2196F3', '#0AA194']
      let seriesList = []
      let data = Object.keys(params)
      data.splice(data.indexOf('Total'), 1)
      data.unshift('Total')
      let myChart = this.$echarts.init(document.getElementById('show-list-b'))
      let dateList = params.Total.map(function (val) {
        return val.counterkey + ':00:00'
      })
      let legendData = []
      data.forEach((ele, index) => {
        let title = ele === 'Total' ? '总量' : ele
        legendData.push(title)
        let list = []
        params[ele].forEach(function (val) {
          list.push(val.countervalue * 100)
        })
        seriesList.push({
          name: title,
          type: 'line',
          smooth: true, // 是否平滑曲线显示
          showSymbol: false,
          symbolSize: 1, // 数据点的大小，[0,0]//b表示宽度和高度
          itemStyle: {
            normal: {
              // color: 'rgba(255,139,119, 0.9)'
              color: colorList[index]
            }
          },
          data: list
        })
      })
      myChart.setOption({
        legend: {
          // top: '10px',
          // right: '10px',
          // backgroundColor: 'rgba(128, 128, 128, 0.2)',
          data: legendData,
          // left: 'right', // 像右对齐
          // orient: 'vertical',
          textStyle: {
            color: '#ffffff'
          },
          bottom: '10px',
          icon: 'circle',
          itemWidth: 12, // 设置宽度
          itemHeight: 10 // 设置高度
        },
        title: [{
          left: '10px',
          top: '10px',
          text: '网关健康状况',
          textStyle: {
            fontWeight: '600',
            fontSize: 15,
            color: '#fff'
          }
        }],
        tooltip: {
          trigger: 'axis', // 触发类型。[ default: 'item' ] :数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用;'axis'坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用
          axisPointer: {
            lineStyle: {
              color: '#57617B'
            }
          },
          formatter: function (params) {
            var relVal = params[0].name
            for (var i = 0, l = params.length; i < l; i++) {
              relVal += '<br/>' + ((params[i].seriesName === 'Total' ? '总量' : params[i].seriesName) + ':' +params[i].value) + '%'
            }
            return relVal
          }
        },
        xAxis: [{
          type: 'category',
          data: dateList,
          boundaryGap: false,
          axisLine: {
            show: true,
            lineStyle: {
              color: '#6C7B8A' //坐标轴线线的颜色
            }
          },
          axisLabel: {
            // margin: 10, // 刻度标签与轴线之间的距离
            textStyle: {
              color: '#6C7B8A',
              fontSize: 10 // 文字的字体大小
            },
            margin: 15,
            align: 'center',
            formatter: function (value,index) {
              return value.split(' ')[0] + '\n' + value.split(' ')[1]
            }
          }
        }],
        yAxis: [{
          axisTick: {
            show: true //是否显示坐标轴刻度
          },
          axisLine: {
            lineStyle: {
              color: '#373C4F' //坐标轴线线的颜色
            }
          },
          axisLabel: {
            margin: 10, //刻度标签与轴线之间的距离
            textStyle: {
              color: '#BDCADB',
              fontSize: 10 //文字的字体大小
            }
          },
          splitLine: {
            show: false,
            lineStyle: {
              color: '#6C7B8A' //分隔线颜色设置
            }
          }
        }],
        grid: [{
          bottom: '100px',
          right: '20px',
          top: '50px',
          left: '45px'
        }],
        // areaStyle: { // 区域填充样式
        //   normal: {
        //     //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
        //     color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
        //       { offset: 0,  color: 'rgba(255,139,119, 0.5)'}, 
        //       { offset: 0.9,  color: 'rgba(254,106,172, 0)'}
        //     ], false),
        //     shadowColor: 'rgba(255,139,119, 0.9)', //阴影颜色
        //     shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
        //   }
        // },
        series: seriesList
      })
    },
    invocationTrend: function (params) {
      let colorList = ['#FFB029', '#946CFF', '#F26186', '#E91E63', '#2196F3', '#0AA194']
      let seriesList = []
      let data = Object.keys(params)
      data.splice(data.indexOf('Total'), 1)
      data.unshift('Total')
      let myChart = this.$echarts.init(document.getElementById('show-list-number'))
      let dateList = params.Total.map(function (val) {
        return val.counterkey + ':00:00'
      })
      let legendData = []
      data.forEach((ele, index) => {
        let list = []
        let title = ele === 'Total' ? '总量' : ele
        legendData.push(title)
        params[ele].forEach(function (val) {
          list.push(val.countervalue)
        })
        seriesList.push({
          name: title,
          type: 'line',
          smooth: true, // 是否平滑曲线显示
          showSymbol: false,
          symbolSize: 1, // 数据点的大小，[0,0]//b表示宽度和高度
          itemStyle: {
            normal: {
              // color: 'rgba(0,234,255, 0.9)'
              color: colorList[index]
            }
          },
          // areaStyle: { // 区域填充样式
          //   normal: {
          //     //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
          //     color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
          //       { offset: 0,  color: 'rgba(0,234,255, 0.5)'}, 
          //       { offset: 0.9,  color: 'rgba(33,205,211, 0)'}
          //     ], false),
          //     shadowColor: 'rgba(53,142,215, 0.9)', //阴影颜色
          //     shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
          //   }
          // },
          data: list
        })
      })
      myChart.setOption({
        legend: {
          // top: '10px',
          // right: '10px',
          // backgroundColor: 'rgba(128, 128, 128, 0.2)',
          data: legendData,
          // left: 'right', // 像右对齐
          // orient: 'vertical',
          textStyle: {
            color: '#ffffff'
          },
          bottom: '10px',
          icon: 'circle',
          itemWidth: 12, // 设置宽度
          itemHeight: 10 // 设置高度
        },
        title: [{
          left: '10px',
          top: '10px',
          text: '网关调用趋势',
          textStyle: {
            fontWeight: '600',
            fontSize: 15,
            color: '#fff'
          }
        }],
        tooltip: {
          trigger: 'axis', //触发类型。[ default: 'item' ] :数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用;'axis'坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用
          axisPointer: {
            lineStyle: {
              color: '#57617B'
            }
          }
        },
        xAxis: [{
          show: true,
          data: dateList,
          // boundaryGap: false,
          axisLine: {
            show: true,
            lineStyle: {
              color: '#6C7B8A' //坐标轴线线的颜色
            }
          },
          axisLabel: {
            textStyle: {
              color: '#6C7B8A',
              fontSize: 10 // 文字的字体大小
            },
            margin: 15, //刻度标签与轴线之间的距离
            align: 'center',
            formatter: function (value,index) {
              return value.split(' ')[0] + '\n' + value.split(' ')[1]
            }
          }
        }],
        yAxis: [{
          axisTick: {
            show: true //是否显示坐标轴刻度
          },
          axisLine: {
            lineStyle: {
              color: '#373C4F' //坐标轴线线的颜色
            }
          },
          axisLabel: {
            margin: 10, //刻度标签与轴线之间的距离
            textStyle: {
              color: '#BDCADB',
              fontSize: 10 //文字的字体大小
            }
          },
          splitLine: {
            show: false,
            lineStyle: {
              color: '#6C7B8A' //分隔线颜色设置
            }
          }
        }],
        grid: [{
          bottom: '100px',
          right: '20px',
          top: '50px',
          left: '45px'
        }],
        series: seriesList
      })
    },
    showHiddenGateway: function (data) {
      this.gatewayStatusDetailShow = data
    },
    showHiddenMonitor: function (data) {
      this.monitorCenterDetailShow = data
    },
    showMonitorWarningDetail: function (val) {
      this.monitorWarningParams = val
      this.monitorWarningDetailShow = true
    },
    showHiddenMonitorWarning: function (data) {
      this.monitorWarningDetailShow = data
    },
    getPandectdata: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/getSumAPIaccess', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.callCount = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
      // self.callCount = sessionStorage.getItem('getSumAPIaccess')
      self.$http.get(self.$api.getApiAddress('/getRegisterRouteNo', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.registerNumber = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
      self.$http.get(self.$api.getApiAddress('/getgwAlarmCount', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.warningNumber = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
      self.$http.get(self.$api.getApiAddress('/getgwAlarmList', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.warningMonitor = res.data.response
          // self.warningMonitor.instance = res.data.response[0].zuulInstance
          // self.warningMonitor.waringingInfo = res.data.response[0].alarmInfomation
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
      self.getGatewayStatusList()
      self.getMonitorStatusList()
    },
    getGatewayStatusList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/queryZuulListInfo', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          let data = []
          self.viewGatewayStatusList = res.data.response
          res.data.response.forEach((ele) => {
            if (ele.zuulStatus !== 'Dead') {
              data.push(ele.zuulInstanceId)
            }
          })
          if (JSON.stringify(data) !== '[]') {
            this.monitorCheckedRadio = data[0]
            this.getMonitorStatus(this.monitorCheckedRadio)
          }
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    },
    getMonitorStatusList: function () {
      let self = this
      self.$http.get(self.$api.getApiAddress('/queryAdminListInfo', 'CESHI_API_HOST')).then((res) => {
        if (res.data.code === 200) {
          self.viewMonitorStatusList = res.data.response
        }
      }).catch((err) => {
        self.$message({message: this.$helper.handleLoginErrorMsg(err), type: 'error'})
      })
    }
  }
}
</script>
<style lang='less' scoped>
@import '../styles/home.page.less';
</style>
<style lang='less'>
@import '../styles/home.page.reset.less';
</style>

