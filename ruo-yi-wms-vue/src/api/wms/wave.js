import request from '@/utils/request'

export function listWave(query) {
  return request({ url: '/wms/wave/list', method: 'get', params: query })
}
export function listWaveNoPage(query) {
  return request({ url: '/wms/wave/listNoPage', method: 'get', params: query })
}
export function getWave(id) {
  return request({ url: '/wms/wave/' + id, method: 'get' })
}
export function addWave(data) {
  return request({ url: '/wms/wave', method: 'post', data: data })
}
export function updateWave(data) {
  return request({ url: '/wms/wave', method: 'put', data: data })
}
export function delWave(id) {
  return request({ url: '/wms/wave/' + id, method: 'delete' })
}
export function startWave(id) {
  return request({ url: '/wms/wave/' + id + '/start', method: 'post' })
}
export function completeWave(id) {
  return request({ url: '/wms/wave/' + id + '/complete', method: 'post' })
}
export function cancelWave(id) {
  return request({ url: '/wms/wave/' + id + '/cancel', method: 'post' })
}
export function listPickingTasks(waveId) {
  return request({ url: '/wms/picking/tasks/' + waveId, method: 'get' })
}
