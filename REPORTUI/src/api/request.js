import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例 - 生产环境使用完整 URL
const request = axios.create({
  baseURL: 'http://47.100.138.89:8110/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 自动添加 token（从 localStorage 读取）
    const token = localStorage.getItem('token') || ''
    
    // GET 请求：添加到 params
    if (config.method === 'get' && token) {
      config.params = config.params || {}
      config.params.token = token
    }
    
    // POST/PUT 请求：添加 token 到 data 中
    if (config.method === 'post' && token && config.data) {
      config.data.token = token
    }
    
    // POST/PUT 请求：如果请求体中有 sign 对象，统一添加 token
    if (config.data && config.data.sign) {
      config.data.sign.token = token
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 根据后端返回格式处理
    // 智问 API 返回格式：{success: true/false, data: ...}
    // 报表 API 返回格式：{success: true/false, serviceStatus: '000', ...}
    if (res.success === false) {
      ElMessage.error(res.message || res.serviceDescription || '请求失败')
      return Promise.reject(new Error(res.message || res.serviceDescription || '请求失败'))
    }
    
    // 兼容报表 API 的 serviceStatus 检查
    if (res.serviceStatus && res.serviceStatus !== '000') {
      ElMessage.error(res.serviceDescription || '请求失败')
      return Promise.reject(new Error(res.serviceDescription || '请求失败'))
    }
    
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
