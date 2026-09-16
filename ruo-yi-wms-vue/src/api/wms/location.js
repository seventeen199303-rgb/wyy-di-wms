import request from '@/utils/request'

// 查询库位列表
export function listLocation(query) {
  return request({
    url: '/wms/location/list',
    method: 'get',
    params: query
  })
}

// 查询库位列表(不分页)
export function listLocationNoPage(query) {
  return request({
    url: '/wms/location/listNoPage',
    method: 'get',
    params: query
  })
}

// 查询库位详细
export function getLocation(id) {
  return request({
    url: '/wms/location/' + id,
    method: 'get'
  })
}

// 新增库位
export function addLocation(data) {
  return request({
    url: '/wms/location',
    method: 'post',
    data: data
  })
}

// 修改库位
export function updateLocation(data) {
  return request({
    url: '/wms/location',
    method: 'put',
    data: data
  })
}

// 删除库位
export function delLocation(id) {
  return request({
    url: '/wms/location/' + id,
    method: 'delete'
  })
}
