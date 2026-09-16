import request from '@/utils/request'

export function listBatch(query) {
  return request({ url: '/wms/batch/list', method: 'get', params: query })
}
export function listBatchNoPage(query) {
  return request({ url: '/wms/batch/listNoPage', method: 'get', params: query })
}
export function getBatch(id) {
  return request({ url: '/wms/batch/' + id, method: 'get' })
}
export function addBatch(data) {
  return request({ url: '/wms/batch', method: 'post', data: data })
}
export function updateBatch(data) {
  return request({ url: '/wms/batch', method: 'put', data: data })
}
export function delBatch(id) {
  return request({ url: '/wms/batch/' + id, method: 'delete' })
}
