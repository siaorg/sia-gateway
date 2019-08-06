(function () {
  window.API = {
    /**
     * vmware部署： 127.0.0.1 ----> nginx的ip地址
     * docker镜像部署： 127.0.0.1 ----> docker容器的宿主机ip
     */
    'CESHI_API_HOST': '127.0.0.1:18086/vv1',
    /**
     * 127.0.0.1 ----> kibana的ip地址
     */
    'CESHI_API_HOST_LOG': '127.0.0.1:5601'
  }
  Object.freeze(window.API)
  Object.defineProperty(window, 'API', {
    configurable: false,
    writable: false
  })
})()
