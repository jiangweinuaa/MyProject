/**
 * IBPUI 服务调用模块
 * 完全复制 V3XUI 的 PostService 调用方式
 * 根据 system 参数自动读取对应服务的 URL 配置
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import { Base64 } from 'js-base64'
import { Md5 } from 'ts-md5'
import Config from '@/config'

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

// 获取服务地址（完全按照 V3XUI PostService 的 getRequestUrl 方式）
function getRequestUrl(system: number, postUrl?: string): string {
  let serviceUrl: string
  
  if (postUrl) {
    serviceUrl = postUrl
  } else {
    switch (system) {
      case 1: // SMS (DCP)
        serviceUrl = (Config as any).sms?.http?.postUrl || ''
        break
      case 2: // CRM
        serviceUrl = (Config as any).crm?.http?.postUrl || ''
        break
      case 3: // CRM Upload
        serviceUrl = (Config as any).crm?.http?.uploadUrl || ''
        break
      case 4: // Supplier
        serviceUrl = (Config as any).supplier?.http?.postUrl || ''
        break
      case 5: // Sale
        serviceUrl = (Config as any).sale?.http?.postUrl || ''
        break
      case 6: // MES
        serviceUrl = (Config as any).mes?.http?.postUrl || ''
        break
      case 7: // EIP
        serviceUrl = (Config as any).eip?.http?.postUrl || ''
        break
      default:
        ElMessage.warning('服务地址配置错误')
        serviceUrl = ''
    }
  }
  
  // HTTPS 自动切换
  if ((Config as any).httpsAuto) {
    if (/https\:/.test(window.location.href)) {
      serviceUrl = serviceUrl.replace(/http\:/, 'https:')
    } else if (/http\:/.test(window.location.href)) {
      serviceUrl = serviceUrl.replace(/https\:/, 'http:')
    }
  }
  
  return serviceUrl
}

// 创建 axios 实例（完全按照 V3XUI 方式）
const service = axios.create({
  timeout: 1800000, // 1800 秒超时（与 V3XUI 一致）
  headers: {
    'Content-Type': 'application/json; charset=UTF-8'
  }
  // 不使用 transformRequest，让 Axios 自动序列化
})

// 请求拦截器
service.interceptors.request.use(
  (config: any) => {
    const userStore = useUserStore()
    const loginInfo = userStore.loginInfo
    
    // 添加公共请求参数（完全按照 V3XUI 方式）
    if (config.data) {
      // data 已经是对象，直接添加参数
      config.data.requestId = generateRequestId()
      config.data.plantType = 'nrc'
      config.data.version = getVersion()
      
      // 是否需要提醒（默认 true）
      if (config.data.remind === undefined) {
        config.data.remind = true
      }
      
      // 登录服务不需要 token
      if (
        config.data.serviceId !== 'DCP_LoginRetail' &&
        config.data.serviceId !== 'DCP_VersionQuery' &&
        config.data.serviceId !== 'DCP_CheckOpenEnterpriseChatLogin_Open'
      ) {
        if (loginInfo) {
          config.data.token = loginInfo.token
          config.data.timestamp = getTimeToMilliseconds()
          config.data.langType = loginInfo.datas?.[0]?.langType || 'zh_CN'
        }
      }
      
      // Authorization header（登录请求不发送）
      if (userStore.authorization && config.data.serviceId !== 'DCP_LoginRetail') {
        config.headers['Authorization'] = userStore.authorization
      }
    }
    // Axios 会自动 JSON.stringify(config.data)
    
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器（完全按照 V3XUI 方式）
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 服务执行失败：success != true
    if (res.success !== true) {
      // 某些服务不需要弹提示
      const noAlertServices = [
        'DCP_CreditPayResultQuery',
        'DCP_MainInfoQuery',
        'DCP_OrderListQuery',
        'OrderPStockInProcess',
        'DCP_PurOrderCreate'
      ]
      
      if (!noAlertServices.includes(res.serviceId) && res.remind !== false) {
        ElMessage.error(res.serviceDescription || res.message || '请求失败')
      }
      
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
  (error: any) => {
    console.error('Response error:', error)
    
    // 处理 NullPointerException
    if (error?.error?.text === 'java.lang.NullPointerException') {
      ElMessage.error('java.lang.NullPointerException')
    } else {
      ElMessage.error('服务执行异常！')
    }
    
    return Promise.reject(error)
  }
)

// 生成 Authorization (完全按照 V3XUI 格式)
export function getAuthorization(opNo: string, password: string): string {
  // 检查 MES 和 EIP 配置（与 V3XUI 一致）
  const cfg = Config as any
  if (
    (!cfg.mes || !cfg.mes.http || !cfg.mes.http.postUrl) &&
    (!cfg.eip || !cfg.eip.http || !cfg.eip.http.postUrl)
  ) {
    return ''
  }
  
  // 登录用户鉴权：用户名 + 冒号 + 密码暗码，utf-8 后转 base64，最后用 Basic 加空格在前面
  const auth = `${opNo}:${password}`
  const encodedAuth = Base64.encode(auth)
  return `Basic ${encodedAuth}`
}

/**
 * 发起服务请求（完全按照 V3XUI PostService 的 post 方法）
 * @param system 1:SMS(DCP) | 2:CRM | 3:CRM Upload | 4:Supplier | 5:Sale | 6:MES | 7:EIP
 * @param postData 服务请求数据 {serviceId, request, ...}
 * @param postUrl 自定义服务地址（可选）
 */
export function post(system: number, postData: any, postUrl?: string): Promise<any> {
  const serviceUrl = getRequestUrl(system, postUrl)
  
  return service.post(serviceUrl, postData)
}

/**
 * 带 header 的请求（用于 MES/EIP 等需要特殊 header 的服务）
 */
export function postWithHeader(
  system: number,
  postData: any,
  serviceId: string,
  postUrl?: string
): Promise<any> {
  const serviceUrl = getRequestUrl(system, postUrl)
  const userStore = useUserStore()
  
  // 获取配置
  let apiUserCode = 'digiwin'
  if (system === 6) { // MES
    apiUserCode = (Config as any).mes?.apiUserCode || 'digiwin'
  } else if (system === 7) { // EIP
    apiUserCode = (Config as any).eip?.apiUserCode || 'digiwin'
  }
  
  // 生成签名
  const sign = Md5.hashStr(JSON.stringify(postData) + apiUserCode)
  
  // 构建 header
  const headers: any = {
    'Content-Type': 'application/json; charset=UTF-8',
    'serviceId': serviceId,
    'version': getVersion(),
    'requestId': generateRequestId(),
    'plantType': system === 6 ? 'MES' : 'EIP',
    'langType': 'zh_CN',
    'apiUserCode': apiUserCode,
    'timestamp': getTimeToMilliseconds(),
    'sign': sign.toString(),
  }
  
  // 添加 authorization
  if (userStore.authorization) {
    headers['authorization'] = userStore.authorization
  }
  
  return service.post(serviceUrl, JSON.stringify(postData), { headers })
}

// 导出默认方法
export default {
  post,
  postWithHeader,
  getAuthorization,
  Config
}
