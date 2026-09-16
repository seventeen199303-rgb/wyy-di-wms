import request from '@/utils/request'

// 库存周转分析
export function listTurnover(query) {
  return request({
    url: '/wms/report/turnover',
    method: 'get',
    params: query
  })
}

// 出入库统计报表
export function listFlow(query) {
  return request({
    url: '/wms/report/flow',
    method: 'get',
    params: query
  })
}

// 出入库月度趋势
export function listFlowTrend(query) {
  return request({
    url: '/wms/report/flow/trend',
    method: 'get',
    params: query
  })
}

// 盘点差异报表
export function listCheckDiff(query) {
  return request({
    url: '/wms/report/check',
    method: 'get',
    params: query
  })
}
