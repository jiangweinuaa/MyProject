import { post, getAuthorization } from '@/utils/PostService'
import { Md5 } from 'ts-md5'
import { useUserStore } from '@/stores/user'

export interface LoginParams {
  eId: string
  opNo: string
  password: string
  codevalue?: string
  langType?: string
  chatUserId?: string
}

export interface LoginResult {
  success: boolean
  serviceDescription?: string
  token: string
  datas: Array<{
    eId: string
    shopId: string
    shopName: string
    opNo: string
    opName: string
    langType: string
    dataParas: Array<{
      paraName: string
      paraValue: string
    }>
    [key: string]: any
  }>
}

/**
 * 用户登录 (完全按照 V3XUI 格式)
 * serviceId: DCP_LoginRetail
 * system: 1 (SMS/DCP)
 */
export function login(data: LoginParams): Promise<LoginResult> {
  // 密码 MD5 加密：Md5.hashStr(loginID + password)
  const passwordMd5 = Md5.hashStr(data.opNo + data.password)
  
  const request: any = {
    eId: data.eId,
    opNo: data.opNo,
    password: passwordMd5,
    langType: data.langType || 'zh_CN'
  }
  
  if (data.chatUserId) {
    request.chatUserId = data.chatUserId
  }
  
  const postData = {
    serviceId: 'DCP_LoginRetail',
    request
  }
  
  // system: 1 = SMS/DCP
  return post(1, postData)
}

/**
 * 登录成功后调用，存储 Authorization
 */
export function storeAuthorization(opNo: string, password: string) {
  const passwordMd5 = Md5.hashStr(opNo + password)
  const authorization = getAuthorization(opNo, passwordMd5)
  const userStore = useUserStore()
  userStore.setAuthorization(authorization)
  return authorization
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return post(1, {
    serviceId: 'DCP_UserInfoQuery',
    request: {}
  })
}

/**
 * 退出登录
 */
export function logout() {
  return post(1, {
    serviceId: 'DCP_Logout',
    request: {}
  })
}

/**
 * 获取菜单权限
 */
export function getMenus() {
  return post(1, {
    serviceId: 'DCP_MenuQuery',
    request: {}
  })
}
