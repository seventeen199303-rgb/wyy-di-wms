import request from '@/utils/request'

export function listSerial(query) {
  return request({ url: '/wms/serial/list', method: 'get', params: query })
}
export function listSerialNoPage(query) {
  return request({ url: '/wms/serial/listNoPage', method: 'get', params: query })
}
export function getSerial(id) {
  return request({ url: '/wms/serial/' + id, method: 'get' })
}
export function addSerial(data) {
  return request({ url: '/wms/serial', method: 'post', data: data })
}
export function updateSerial(data) {
  return request({ url: '/wms/serial', method: 'put', data: data })
}
export function delSerial(id) {
  return request({ url: '/wms/serial/' + id, method: 'delete' })
}
