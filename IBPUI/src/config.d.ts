/**
 * IBPUI 配置类型定义
 */

export interface HttpConfig {
  postUrl: string
  locationUrl?: string
  uploadUrl?: string
  emailUrl?: string
  goodImagesUrl?: string
  reportImagesUrl?: string
  dualUrl?: string
  GDkey?: string
  documentUploadUrl?: string
  basicmarketUrl?: string
  basicmarketSign?: string
}

export interface SystemConfig {
  http: HttpConfig
  apiUserCode?: string
}

export interface AppConfigType {
  sms: SystemConfig
  crm: SystemConfig
  supplier: SystemConfig
  sale: SystemConfig
  mes: SystemConfig
  eip: SystemConfig
  isWechat: boolean
  httpsAuto: boolean
}

declare global {
  interface Window {
    AppConfig?: AppConfigType
  }
}

export {}
