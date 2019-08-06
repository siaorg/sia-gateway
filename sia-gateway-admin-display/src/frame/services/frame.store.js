'use strict'

const frameStore = {}

/* @usage : store state data shcema
 this.$store.state.frame  */
frameStore.state = {
  // 当前网管组
  currentGatewayGroup: sessionStorage.getItem('currentGatewayGroup') || ''
}

/* @usage : unit operation
 this.$store.commit('CHANGE_MENU') */
frameStore.mutations = {
  'CURREN_GATEWAY_GROUP' (state, currentGatewayGroup) {
    state.currentGatewayGroup = currentGatewayGroup
    sessionStorage.setItem('currentGatewayGroup', currentGatewayGroup)
  }
}

/* @usage : based on state ,  return state's length or filter state data */
frameStore.getters = {

}

/* @usage : basic the operation for view component , this.$store.dispatch('CHANGE_MENU_ACTION') */
frameStore.actions = {
  'CURREN_GATEWAY_GROUP_ACTION' ({ commit }, string) {
    return new Promise((resolve, reject) => {
      commit('CURREN_GATEWAY_GROUP', string)
      resolve()
    })
  }
}

export default frameStore
