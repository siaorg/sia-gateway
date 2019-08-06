var merge = require('webpack-merge')
var prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  CESHI_API_HOST: "'10.143.135.138:8090'",
  // CESHI_API_HOST: "'10.10.169.78:8090'",
  // CESHI_API_HOST: "'10.143.135.183:8090'",
  // CESHI_API_HOST: "'10.10.168.19:8090'",
  CESHI_API_HOST_LOG: "'10.143.131.86:5601'"
})
