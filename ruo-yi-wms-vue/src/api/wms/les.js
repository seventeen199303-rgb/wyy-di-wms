import request from '@/utils/request'

// ==================== 发货计划 ====================
export function listShipPlan(query) {
  return request({ url: '/wms/shipPlan/list', method: 'get', params: query })
}

export function listShipPlanNoPage(query) {
  return request({ url: '/wms/shipPlan/listNoPage', method: 'get', params: query })
}

export function getShipPlan(id) {
  return request({ url: '/wms/shipPlan/' + id, method: 'get' })
}

export function listShipPlanDetails(id) {
  return request({ url: '/wms/shipPlan/' + id + '/details', method: 'get' })
}

export function addShipPlan(data) {
  return request({ url: '/wms/shipPlan', method: 'post', data: data })
}

export function updateShipPlan(data) {
  return request({ url: '/wms/shipPlan', method: 'put', data: data })
}

export function cancelShipPlan(id) {
  return request({ url: '/wms/shipPlan/' + id + '/cancel', method: 'post' })
}

export function delShipPlan(id) {
  return request({ url: '/wms/shipPlan/' + id, method: 'delete' })
}

// ==================== 装车单 ====================
export function listLoadOrder(query) {
  return request({ url: '/wms/loadOrder/list', method: 'get', params: query })
}

export function listLoadOrderNoPage(query) {
  return request({ url: '/wms/loadOrder/listNoPage', method: 'get', params: query })
}

export function getLoadOrder(id) {
  return request({ url: '/wms/loadOrder/' + id, method: 'get' })
}

export function listLoadOrderDetails(id) {
  return request({ url: '/wms/loadOrder/' + id + '/details', method: 'get' })
}

export function addLoadOrder(data) {
  return request({ url: '/wms/loadOrder', method: 'post', data: data })
}

export function updateLoadOrder(data) {
  return request({ url: '/wms/loadOrder', method: 'put', data: data })
}

export function departLoadOrder(id) {
  return request({ url: '/wms/loadOrder/' + id + '/depart', method: 'post' })
}

export function signLoadOrder(id, signName) {
  return request({ url: '/wms/loadOrder/' + id + '/sign', method: 'post', params: { signName } })
}

export function abnormalLoadOrder(id, remark) {
  return request({ url: '/wms/loadOrder/' + id + '/abnormal', method: 'post', params: { remark } })
}

export function recoverLoadOrder(id) {
  return request({ url: '/wms/loadOrder/' + id + '/recover', method: 'post' })
}

export function delLoadOrder(id) {
  return request({ url: '/wms/loadOrder/' + id, method: 'delete' })
}
