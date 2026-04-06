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
    // 可以在这里添加 token 等
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
    if (res.success === false || res.serviceStatus !== '000') {
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
