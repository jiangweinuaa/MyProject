import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const authorization = ref<string>('')
  const loginInfo = ref<any>(null)
  const userInfo = ref<any>(null)
  const permissions = ref<string[]>([])

  function setToken(newToken: string) {
    token.value = newToken
    // 同时存储到 localStorage（供路由守卫使用）
    localStorage.setItem('token', newToken)
  }

  function setAuthorization(auth: string) {
    authorization.value = auth
  }

  function setLoginInfo(info: any) {
    loginInfo.value = info
  }

  function setUserInfo(info: any) {
    userInfo.value = info
  }

  function setPermissions(perms: string[]) {
    permissions.value = perms
  }

  function logout() {
    token.value = ''
    authorization.value = ''
    loginInfo.value = null
    userInfo.value = null
    permissions.value = []
    // 清除 localStorage
    localStorage.removeItem('token')
  }

  return {
    token,
    authorization,
    loginInfo,
    userInfo,
    permissions,
    setToken,
    setAuthorization,
    setLoginInfo,
    setUserInfo,
    setPermissions,
    logout
  }
})
