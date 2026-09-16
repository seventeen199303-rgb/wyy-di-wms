import request from '@/utils/request'

// 安全库存预警列表
export function listSafetyStockWarning(query) {
  return request({
    url: '/wms/warning/safetyStock/list',
    method: 'get',
    params: query
  })
}

// 超储预警列表
export function listOverStockWarning(query) {
  return request({
    url: '/wms/warning/overStock/list',
    method: 'get',
    params: query
  })
}

// 呆滞料列表
export function listSlowMoving(query) {
  return request({
    url: '/wms/warning/slowMoving/list',
    method: 'get',
    params: query
  })
}
