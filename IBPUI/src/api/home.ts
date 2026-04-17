import { post } from '@/utils/PostService'

export interface ToDoItem {
  modularNo: string
  modularName: string
  proName: string
  todoCount: number
  [key: string]: any
}

export interface MainInfo {
  monitor: any[]
  monitorSpace: any[]
  diskSpace: any[]
  etlDown: any[]
  wsDown: any[]
  erpDown: any[]
  etlUp: any[]
  wsUp: any[]
  erpUp: any[]
  eDate: any[]
  job: any[]
  machine: any[]
  version: any[]
  [key: string]: any
}

/**
 * 获取待办事项列表
 * serviceId: DCP_ToDoListQueryNew
 */
export function getToDoList() {
  return post(1, {
    serviceId: 'DCP_ToDoListQueryNew',
    request: {}
  })
}

/**
 * 获取服务器当前日期
 * serviceId: DCP_ServerCurDateQuery
 */
export function getServerDate() {
  return post(1, {
    serviceId: 'DCP_ServerCurDateQuery',
    request: {}
  })
}

/**
 * 获取系统监控信息
 * serviceId: DCP_MainInfoQuery
 */
export function getMainInfo() {
  return post(1, {
    serviceId: 'DCP_MainInfoQuery',
    request: {}
  })
}

/**
 * 获取版本信息
 * serviceId: CMS_VersionList
 */
export function getVersionList() {
  return post(1, {
    serviceId: 'CMS_VersionList',
    request: {}
  })
}

/**
 * 获取异常信息
 * serviceId: DCP_MainErrorInfoQuery
 */
export function getMainErrorInfo() {
  return post(1, {
    serviceId: 'DCP_MainErrorInfoQuery',
    request: {}
  })
}

/**
 * 删除异常信息
 * serviceId: DCP_MainErrorInfoDelete
 */
export function deleteMainErrorInfo(id: string) {
  return post(1, {
    serviceId: 'DCP_MainErrorInfoDelete',
    request: { id }
  })
}

/**
 * 更新收藏
 * serviceId: DCP_CollectionUpdate
 */
export function updateCollection(data: any) {
  return post(1, {
    serviceId: 'DCP_CollectionUpdate',
    request: data
  })
}

/**
 * 获取 AI AccessToken
 * serviceId: DCP_GetAI_AccessToken
 */
export function getAIAccessToken() {
  return post(1, {
    serviceId: 'DCP_GetAI_AccessToken',
    request: {}
  })
}
