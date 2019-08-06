'use strict'
const helperProvider = {}
helperProvider.handleLoginErrorMsg = function (response) {
  if (response.message && response.message.indexOf('Network Error') > -1) {
    return '请求超时！'
  }
  switch (response.data.code) {
    case 400:
      return '参数错误！'
    case 401:
      return '限制调用！'
    case 402:
      return 'token 过期！'
    case 403:
      return '禁止访问！'
    case 404:
      return '资源没找到！'
    case 406:
      return '服务降级中！'
    case 407:
      return '路由ID已存在！'
    case 408:
      return '匹配路径已存在！'
    case 500:
      return '服务器错误！'
    default:
      return '服务未响应！'
  }
}

export default helperProvider
