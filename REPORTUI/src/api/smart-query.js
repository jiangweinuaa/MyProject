import request from './request'

/**
 * 获取会话列表
 */
export function getConversationList(params) {
  return request({
    url: '/conversation/list',
    method: 'get',
    params
  })
}

/**
 * 获取会话历史
 */
export function getConversationHistory(params) {
  return request({
    url: '/conversation/history',
    method: 'get',
    params
  })
}

/**
 * 智问查询
 */
export function nlQuery(data) {
  return request({
    url: '/nl-query/query',
    method: 'post',
    data
  })
}

/**
 * 删除会话
 */
export function deleteConversation(sessionId) {
  return request({
    url: `/conversation/${sessionId}`,
    method: 'delete'
  })
}

/**
 * 更新会话标题
 */
export function updateConversationTitle(sessionId, title) {
  return request({
    url: `/conversation/${sessionId}/title`,
    method: 'put',
    data: { title }
  })
}
