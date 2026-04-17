import axios, { AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import { Base64 } from 'js-base64'
import config from '@/config'

// 生成 GUID (16 位随机字符)
function generateRequestId(): string {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
  let uuid = ''
  for (let i = 0; i < 16; i++) {
    uuid += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return uuid
}

// 获取当前时间戳（精确到毫秒）格式：yyyyMMddHHmmssSSS
function getTimeToMilliseconds(): string {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  const ms = String(now.getMilliseconds()).padStart(3, '0')
  return `${year}${month}${day}${hours}${minutes}${seconds}${ms}`
}

// 获取版本号
function getVersion(): string {
  const cookies = document.cookie.split('; ')
  let version = '3.0'
  let versionTime = ''
  for (const cookie of cookies) {
    if (cookie.startsWith('version=')) {
      version = cookie.split('=')[1]
    } else if (cookie.startsWith('vTime=')) {
      versionTime = cookie.split('=')[1]
    }
  }
  return `${version}-${versionTime}`
}

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || (config as any).sms?.http?.postUrl?.replace('/post', '') || 'http://localhost:8080',
  timeout: 180000, // 180 秒超时
  headers: {
    'Content-Type': 'application/json; charset=UTF-8'
  }
})

// 请求拦截器 - 按照 V3XUI PostService 格式
service.interceptors.request.use(
  (config: any) => {
    const userStore = useUserStore()
    const loginInfo = userStore.loginInfo
    
    // 添加公共请求参数
    if (config.data) {
      config.data.requestId = generateRequestId()
      config.data.plantType = 'nrc'
      config.data.version = getVersion()
      
      // 登录服务不需要 token
      if (config.data.serviceId !== 'DCP_LoginRetail' && config.data.serviceId !== 'DCP_VersionQuery') {
        if (loginInfo) {
          config.data.token = loginInfo.token
          config.data.timestamp = getTimeToMilliseconds()
          config.data.langType = loginInfo.datas?.[0]?.langType || 'zh_CN'
        }
      }
    }
    
    // Authorization header
    if (userStore.authorization) {
      config.headers['Authorization'] = userStore.authorization
    }
    
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 按照 V3XUI PostService 格式
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 服务执行失败：success != true
    if (res.success !== true) {
      ElMessage.error(res.serviceDescription || res.message || '请求失败')
      
      // 登录超时跳转
      if (res.serviceDescription === '登录超时') {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
        window.location.reload()
      }
      
      return Promise.reject(new Error(res.serviceDescription || res.message || '请求失败'))
    }
    
    return res
  },
  (error: AxiosError) => {
    console.error('Response error:', error)
    
    // 处理 NullPointerException
    if ((error as any)?.error?.text === 'java.lang.NullPointerException') {
      ElMessage.error('java.lang.NullPointerException')
    } else {
      ElMessage.error('服务执行异常！')
    }
    
    return Promise.reject(error)
  }
)

// 生成 Authorization (按照 V3XUI 格式：Basic + base64(opNo:password))
export function getAuthorization(opNo: string, password: string): string {
  const auth = `${opNo}:${password}`
  const encodedAuth = Base64.encode(auth)
  return `Basic ${encodedAuth}`
}

// 封装请求方法 (按照 V3XUI PostService 格式)
export const http = {
  post<T = any>(serviceId: string, request: any, _system: number = 1): Promise<T> {
    const postData: any = {
      serviceId,
      request
    }
    return service.post('/post', postData)
  },
  
  get<T = any>(serviceId: string, request: any = {}, _system: number = 1): Promise<T> {
    const postData: any = {
      serviceId,
      request
    }
    return service.post('/post', postData)
  },
  
  put<T = any>(serviceId: string, request: any, _system: number = 1): Promise<T> {
    const postData: any = {
      serviceId,
      request
    }
    return service.post('/post', postData)
  },
  
  delete<T = any>(serviceId: string, request: any = {}, _system: number = 1): Promise<T> {
    const postData: any = {
      serviceId,
      request
    }
    return service.post('/post', postData)
  }
}

export default service
