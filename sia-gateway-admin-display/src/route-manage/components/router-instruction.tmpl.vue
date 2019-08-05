<template>
  <div class="mask-router-instruction" id="mask">
      <div class="mask-content" id="mask-content">
        <div class="mask-main-title">
          <span>路由信息说明</span>
          <i class="close-icon" @click="showHiddenRouterInstructoin"></i>
        </div>
        <div class="info">
          <p>
            <span>路由ID：</span>
            <span>标识路由的唯一ID，唯一主键，可以根据路由ID查找路由，路由ID不能够重复。</span>
          </p>
          <p>
            <span>网关集群组名：</span>
            <span>网关集群标识这个新建的路由归属在那个网关集群下面， 默认现在我们只有一个网关集群（zuulgw-group1）</span>
          </p>
          <p>
            <span>应用名称：</span>
            <span>应用名称是标识路由的别名，是非必选项</span>
          </p>
          <p>
            <span>匹配路径：</span>
            <span>新建路由的路径匹配Patten(例如/foo/**)，所有发到 /foo/**路径下的请求都会转发到这个路由下面。</span>
          </p>
          <p>
            <span>后端服务策略：</span>
            <span>
                后端路由有三种策略 （只能选其中一种策略）--</br>
                1)、一种是SERVICEID策略：针对链接到Eureka上的应用，根据配置的ServiceID，网关会动态匹配一个后端服务</br>
                2)、一种是URL策略： 针对非Eureka上的应用，根据配置的URL，映射到匹配的URL后端服务上。</br>
                3)、一种是多IP:PORT路由策略： 针对非Eureka上的应用，可以选择多个IP:PORT通过‘，’分割的方式实现路由负载， 目前默认使用RoundRobin方式负载均衡。 例如：192.168.1.3:8070, 192.168.1.2:8090 
            </span>
          </p>
          <p>
            <span>后端服务ID：</span>
            <span>匹配一个新建路由的后端服务唯一标识。</span>
          </p>
          <p>
            <span>后端服务URL：</span>
            <span>匹配一个新建路由的后端服务URL物理地址</span>
          </p>
          <p>
            <span>LISTOFSERVER中的URL格式：</span>
            <span>10.2.3.4:9090,10.2.3.3:8080</span>
          </p>
          <p>
            <span>前缀是否生效：</span>
            <span>
                前缀生效开关标识这个路由在转发时是否需要（删除）</br>前缀路径举例如下：</br>
                1)、当stripPrefix=true的时候 （http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/user/list）</br> 
                2)、当stripPrefix=false的时候（http://127.0.0.1:8181/api/user/list -> http://192.168.1.100:8080/api/user/list）  
            </span>
          </p>
          <p>
            <span>是否重试：</span>
            <span>重试是否生效开关</span>
          </p>
          <!--<p>
            <span>描述：</span>
            <span>路由的描述说明。是Optianl选项。</span>
          </p>-->
        </div>
      </div>
  </div>
</template>

<script>
export default {
  name: 'RouterInstructionTmpls',
  methods: {
    showHiddenRouterInstructoin: function () {
      this.$emit('showHiddenRouterInstructoin', false)
    }
  }
}
</script>
<style lang="less" scoped>
@import '../styles/common/router-instruction.tmpl.less';
</style>
<style lang="less">
@import '../styles/common/router-instruction.tmpl.reset.less';
</style>
