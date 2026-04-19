/**
 * AI 报表设计器 API
 * 使用原生 fetch，不依赖 axios
 */

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://47.100.138.89:8110/api'

/**
 * 获取 token
 */
function getToken() {
  // 优先从 URL 参数获取，其次从 localStorage
  const urlParams = new URLSearchParams(window.location.search)
  const urlToken = urlParams.get('token')
  const localToken = localStorage.getItem('token')
  return urlToken || localToken
}

/**
 * 通用请求处理
 */
async function request(url, options = {}) {
  const token = getToken()
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json'
    }
  }
  
  if (token) {
    defaultOptions.headers.Authorization = `Bearer ${token}`
  }
  
  const response = await fetch(url, { ...defaultOptions, ...options })
  return await response.json()
}

/**
 * 对话生成（复用智问 API）
 */
export function chat(data) {
  return request(`${API_BASE}/nl-query/query`, {
    method: 'POST',
    body: JSON.stringify({
      sessionId: data.sessionId,
      question: data.question,
      sign: {
        token: data.token || getToken()
      }
    })
  })
}

/**
 * 保存报表
 */
export function saveReport(data) {
  return request(`${API_BASE}/designer/save`, {
    method: 'POST',
    body: JSON.stringify({
      ...data,
      token: data.token || getToken()
    })
  })
}

/**
 * 获取菜单树
 */
export function getMenuTree() {
  return request(`${API_BASE}/designer/menu`)
}

/**
 * 获取报表详情
 */
export function getReport(reportId) {
  return request(`${API_BASE}/designer/report/${reportId}`)
}

/**
 * 删除报表
 */
export function deleteReport(reportId) {
  return request(`${API_BASE}/designer/report/${reportId}`, {
    method: 'DELETE'
  })
}

/**
 * 获取报表列表
 */
export function getReportList() {
  return request(`${API_BASE}/designer/reports`)
}

/**
 * 获取报表详情
 */
export function getReportDetail(reportId) {
  return request(`${API_BASE}/designer/report/${reportId}`)
}
