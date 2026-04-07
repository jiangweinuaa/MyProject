import request from './request'

/**
 * 用户登录
 * @param {Object} params - 登录参数
 * @param {string} params.opno - 操作员编号
 * @param {string} params.password - 密码
 * @returns {Promise}
 */
export function login(params) {
  return request({
    url: '/login',
    method: 'post',
    data: params
  }).then(res => {
    // 登录成功后保存 token
    if (res.success && res.datas && res.datas.token) {
      localStorage.setItem('token', res.datas.token)
      localStorage.setItem('opno', res.datas.opno)
      localStorage.setItem('eid', res.datas.eid)
    }
    return res
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  const token = localStorage.getItem('token')
  return request({
    url: '/logout',
    method: 'post',
    data: { token }
  }).then(res => {
    // 退出成功后清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('opno')
    localStorage.removeItem('eid')
    return res
  })
}

/**
 * 验证 token
 * @returns {Promise}
 */
export function verifyToken() {
  const token = localStorage.getItem('token')
  return request({
    url: '/token/verify',
    method: 'post',
    data: { token }
  })
}

/**
 * 获取当前用户信息
 * @returns {Object}
 */
export function getCurrentUser() {
  return {
    token: localStorage.getItem('token'),
    opno: localStorage.getItem('opno'),
    eid: localStorage.getItem('eid')
  }
}
