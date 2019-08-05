'use strict'
import { RegExpressions } from './regExpressions'
const validator = {}

validator.required = function (tips) {
  return {required: true, message: tips, trigger: 'blur'}
}

validator.range = function (begin, to, tips) {
  return {min: begin, max: to, message: tips, trigger: 'blur'}
}

// type : ['date','array','number']
validator.range = function (type, tips) {
  return {type: type, required: true, message: tips, trigger: 'change'}
}

/**
 * 校验字段（字段在非空时校验）
 * @param regName（regExpressions 文件中相应的验证正则表达式对象的key）
 * @return regExpressions 文件中预定义的错误信息
 */
validator.custom = function (regName) {
  return {
    validator: (rule, value, callback) => {
      if (value !== '') {
        var pattern = new RegExp(RegExpressions[regName].reg)
        if (!pattern.test(value)) {
          callback(new Error(RegExpressions[regName].error_msg))
        }
        callback()
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
}
export default validator
