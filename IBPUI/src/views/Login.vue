<template>
  <div class="login-container">
    <!-- 背景视频 -->
    <video ref="videoBg" class="video-bg" autoplay loop muted playsinline>
      <source src="/assets/login-bg.mp4" type="video/mp4" />
    </video>

    <!-- 左侧 Logo 区域 -->
    <div class="login-left">
      <img class="login-logo" src="/assets/login-logo.png" alt="Logo" />
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-right">
      <div class="login-wrap">
        <div class="login-box">
          <h2 class="login-title">欢迎登录鼎捷流通智汇平台</h2>

          <!-- 企业编码 -->
          <div class="form-item">
            <div class="input-group">
              <span class="input-prefix">
                <img src="/assets/icon_company_selected.svg" alt="" />
              </span>
              <input
                v-model="loginForm.companyNO"
                type="text"
                class="login-input"
                maxlength="5"
                placeholder="企业编码"
                autocomplete="off"
                @keyup.enter="moveNext(0)"
                @focus="btnCloseShow.companyNO = true"
                @blur="btnCloseShow.companyNO = false"
              />
              <span
                v-if="loginForm.companyNO && btnCloseShow.companyNO"
                class="close-circle"
                @click="loginForm.companyNO = ''"
              >
                <svg viewBox="0 0 1024 1024" width="16" height="16">
                  <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                  <path d="M568.2 512l137.9-137.9c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L512 476.8 363.2 328c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l137.9 137.9-137.9 137.9c-12.5 12.5-12.5 32.8 0 45.3 6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4l137.9-137.9 137.9 137.9c6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4c12.5-12.5 12.5-32.8 0-45.3L568.2 512z" fill="currentColor"/>
                </svg>
              </span>
            </div>
          </div>

          <!-- 账号 -->
          <div class="form-item">
            <div class="input-group">
              <span class="input-prefix">
                <img src="/assets/icon_user_selected.svg" alt="" />
              </span>
              <input
                v-model="loginForm.username"
                type="text"
                class="login-input"
                maxlength="10"
                placeholder="账号"
                autocomplete="off"
                @keyup.enter="moveNext(1)"
                @focus="btnCloseShow.loginID = true"
                @blur="btnCloseShow.loginID = false"
              />
              <span
                v-if="loginForm.username && btnCloseShow.loginID"
                class="close-circle"
                @click="loginForm.username = ''"
              >
                <svg viewBox="0 0 1024 1024" width="16" height="16">
                  <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                  <path d="M568.2 512l137.9-137.9c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L512 476.8 363.2 328c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l137.9 137.9-137.9 137.9c-12.5 12.5-12.5 32.8 0 45.3 6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4l137.9-137.9 137.9 137.9c6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4c12.5-12.5 12.5-32.8 0-45.3L568.2 512z" fill="currentColor"/>
                </svg>
              </span>
            </div>
          </div>

          <!-- 密码 -->
          <div class="form-item">
            <div class="input-group">
              <span class="input-prefix">
                <img src="/assets/icon_password_default.svg" alt="" />
              </span>
              <input
                v-model="loginForm.password"
                type="password"
                class="login-input"
                maxlength="10"
                placeholder="密码"
                autocomplete="off"
                @keyup.enter="moveNext(2)"
                @focus="btnCloseShow.password = true"
                @blur="btnCloseShow.password = false"
              />
              <span
                v-if="loginForm.password && btnCloseShow.password"
                class="close-circle"
                @click="loginForm.password = ''"
              >
                <svg viewBox="0 0 1024 1024" width="16" height="16">
                  <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                  <path d="M568.2 512l137.9-137.9c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L512 476.8 363.2 328c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l137.9 137.9-137.9 137.9c-12.5 12.5-12.5 32.8 0 45.3 6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4l137.9-137.9 137.9 137.9c6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4c12.5-12.5 12.5-32.8 0-45.3L568.2 512z" fill="currentColor"/>
                </svg>
              </span>
            </div>
          </div>

          <!-- 验证码 -->
          <div class="form-item">
            <div class="verify-code-row">
              <div class="input-group">
                <span class="input-prefix">
                  <img src="/assets/verificationCode.svg" alt="" />
                </span>
                <input
                  v-model="loginForm.codevalue"
                  type="text"
                  class="login-input"
                  maxlength="4"
                  placeholder="验证码"
                  autocomplete="off"
                  @keyup.enter="handleLogin"
                  @focus="btnCloseShow.codevalue = true"
                  @blur="btnCloseShow.codevalue = false"
                />
                <span
                  v-if="loginForm.codevalue && btnCloseShow.codevalue"
                  class="close-circle"
                  @click="loginForm.codevalue = ''"
                >
                  <svg viewBox="0 0 1024 1024" width="16" height="16">
                    <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                    <path d="M568.2 512l137.9-137.9c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L512 476.8 363.2 328c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l137.9 137.9-137.9 137.9c-12.5 12.5-12.5 32.8 0 45.3 6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4l137.9-137.9 137.9 137.9c6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4c12.5-12.5 12.5-32.8 0-45.3L568.2 512z" fill="currentColor"/>
                  </svg>
                </span>
              </div>
              <input
                type="button"
                class="verify-code"
                :value="loginForm.code"
                @click="createCode"
              />
            </div>
          </div>

          <!-- 登录按钮 -->
          <div class="form-item">
            <button
              class="login-btn"
              :class="allowClass"
              :disabled="disabled"
              @click="handleLogin"
            >
              {{ logTxt }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <div class="login-footer">
      <p class="footer-text">Copyright ©2022-2019 鼎捷软件 All Rights Reserved</p>
    </div>

    <!-- 企业微信登录弹窗 -->
    <div v-if="isWorkWechatLogin" class="wechat-modal">
      <div class="modal-container">
        <div id="weChatCode" class="modal-content"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { login, storeAuthorization } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const videoBg = ref<HTMLVideoElement>()

const loginForm = reactive({
  companyNO: '',
  username: '',
  password: '',
  codevalue: '',
  code: ''
})

const btnCloseShow = reactive({
  companyNO: false,
  loginID: false,
  password: false,
  codevalue: false
})

const allowClass = ref('ok')
const disabled = ref(false)
const logTxt = ref('登 录')
const isWorkWechatLogin = ref(false)

// 生成验证码
const createCode = () => {
  let code = ''
  const random = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
  for (let i = 0; i < 4; i++) {
    const index = Math.floor(Math.random() * 10)
    code += random[index]
  }
  loginForm.code = code
}

// 回车切换焦点
const moveNext = (index: number) => {
  const inputs = ['companyNO', 'username', 'password', 'codevalue']
  const nextIndex = index + 1
  if (nextIndex < inputs.length) {
    // 简单实现，实际需要获取 DOM 元素 focus
  }
}

// 登录验证
const handleLogin = async () => {
  // 验证企业编码
  if (!loginForm.companyNO || loginForm.companyNO.trim() === '') {
    ElMessage.warning('企业编码不可为空')
    return
  }

  // 验证账号
  if (!loginForm.username || loginForm.username.trim() === '') {
    ElMessage.warning('账号不可为空')
    return
  }

  // 验证密码
  if (!loginForm.password || loginForm.password.trim() === '') {
    ElMessage.warning('密码不可为空')
    return
  }

  // 验证验证码
  if (!loginForm.codevalue || loginForm.codevalue.trim() === '') {
    ElMessage.warning('验证码不可为空')
    return
  }

  // 验证码校验
  if (loginForm.codevalue !== loginForm.code) {
    ElMessage.warning('验证码输入错误，请重新输入！')
    createCode()
    loginForm.codevalue = ''
    return
  }

  // 开始登录
  logTxt.value = '正在登录'
  allowClass.value = 'busy'
  disabled.value = true

  try {
    const res = await login({
      eId: loginForm.companyNO,
      opNo: loginForm.username,
      password: loginForm.password,
      codevalue: loginForm.codevalue,
      langType: 'zh_CN'
    })

    if (res.success && res.datas && res.datas[0]) {
      const userData = res.datas[0]

      // 存储登录信息
      userStore.setToken(res.token)
      userStore.setLoginInfo({
        token: res.token,
        datas: [userData]
      })
      userStore.setUserInfo({
        username: userData.opNo,
        name: userData.opName || userData.opNo,
        eId: userData.eId,
        shopId: userData.shopId,
        shopName: userData.shopName,
        langType: userData.langType,
        dataParas: userData.dataParas || [],
        myPower: userData.myPower || [] // 菜单权限（与 V3XUI 一致）
      })

      // 存储 Authorization（与 V3XUI 一致，登录成功后才存储）
      storeAuthorization(loginForm.username, loginForm.password)

      ElMessage.success('登录成功')

      // 设置 cookie
      setCookie('companyNo', loginForm.companyNO, 1)
      setCookie('opNo', loginForm.username, 1)

      router.push('/')
    } else {
      // 登录失败，恢复状态
      logTxt.value = '登 录'
      allowClass.value = 'ok'
      disabled.value = false
      createCode()
      loginForm.codevalue = ''
    }
  } catch (error) {
    console.error('登录失败:', error)
    logTxt.value = '登 录'
    allowClass.value = 'ok'
    disabled.value = false
    createCode()
    loginForm.codevalue = ''
  }
}

// 设置 cookie
const setCookie = (name: string, value: string, days: number) => {
  const d = new Date()
  d.setTime(d.getTime() + days * 24 * 60 * 60 * 1000)
  const expires = 'expires=' + d.toUTCString()
  document.cookie = `${name}=${value}; ${expires}`
}

// 播放视频
const playVideo = () => {
  if (videoBg.value) {
    videoBg.value.muted = true
    const playPromise = videoBg.value.play()
    if (playPromise !== undefined) {
      playPromise.catch((error) => {
        // 浏览器阻止自动播放，忽略错误
        console.log('视频自动播放被阻止:', error)
      })
    }
  }
}

onMounted(() => {
  createCode()
  playVideo()

  // 从 cookie 读取记住的账号
  const cookies = document.cookie.split('; ')
  for (const cookie of cookies) {
    if (cookie.startsWith('opNo=')) {
      loginForm.username = cookie.split('=')[1]
    } else if (cookie.startsWith('companyNo=')) {
      loginForm.companyNO = cookie.split('=')[1]
    }
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #f0f2f5;
}

.video-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.login-left {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;

  .login-logo {
    max-width: 400px;
    max-height: 400px;
  }
}

.login-right {
  position: absolute;
  top: 0;
  right: 0;
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;

  .login-wrap {
    position: absolute;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    .login-box {
      width: 420px;
      padding: 40px;
      background: #fff;
      border-radius: 15px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

      .login-title {
        text-align: center;
        font-size: 24px;
        color: #333;
        margin-bottom: 30px;
        font-weight: 500;
      }
    }
  }
}

.form-item {
  margin-bottom: 20px;

  .input-group {
    position: relative;
    display: flex;
    align-items: center;
    height: 40px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    padding: 0 12px;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409eff;
    }

    &:focus-within {
      border-color: #409eff;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }

    .input-prefix {
      margin-right: 8px;

      img {
        width: 16px;
        height: 16px;
      }
    }

    .login-input {
      flex: 1;
      border: none;
      outline: none;
      font-size: 14px;
      height: 100%;

      &::placeholder {
        color: #c0c4cc;
      }
    }

    .close-circle {
      cursor: pointer;
      color: #c0c4cc;
      transition: color 0.3s;

      &:hover {
        color: #909399;
      }

      svg {
        display: block;
      }
    }
  }
}

.verify-code-row {
  display: flex;
  gap: 12px;

  .input-group {
    flex: 1;
  }

  .verify-code {
    width: 120px;
    height: 40px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background: #f5f7fa;
    font-size: 16px;
    letter-spacing: 4px;
    cursor: pointer;
    text-align: center;
    color: #606266;
    font-weight: bold;

    &:hover {
      background: #e9e9eb;
    }
  }
}

.login-btn {
  width: 100%;
  height: 40px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &.ok {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;

    &:hover {
      opacity: 0.9;
    }
  }

  &.busy {
    background: #a0cfff;
    color: #fff;
    cursor: not-allowed;
  }
}

.login-footer {
  position: absolute;
  bottom: 20px;
  left: 0;
  width: 100%;
  text-align: center;
  z-index: 1;

  .footer-text {
    font-size: 12px;
    color: #828080;
    margin: 5px 0;
  }
}

.wechat-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal-container {
    background: #fff;
    padding: 30px;
    border-radius: 8px;

    .modal-content {
      width: 300px;
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

// ========== 移动端适配 ==========
@media screen and (max-width: 768px) {
  .login-left {
    display: none;
  }

  .login-right {
    position: relative;
    width: 100%;
    left: 0;
  }

  .login-wrap {
    padding: 20px 15px;
    box-sizing: border-box;

    .login-box {
      width: 100% !important;
      max-width: 420px;
      padding: 30px 20px !important;
    }
  }

  .login-title {
    font-size: 20px !important;
    margin-bottom: 20px !important;
  }

  .form-item {
    margin-bottom: 15px !important;

    .input-group {
      height: 44px !important;
    }
  }

  .verify-code-row {
    .verify-code {
      width: 100px !important;
      font-size: 14px !important;
    }
  }

  .login-btn {
    height: 44px !important;
    font-size: 16px !important;
  }

  .login-footer {
    bottom: 15px;

    .footer-text {
      font-size: 11px;
    }
  }
}

// 小屏幕手机
@media screen and (max-width: 375px) {
  .login-wrap {
    padding: 15px 10px !important;

    .login-box {
      padding: 25px 15px !important;
    }
  }

  .login-title {
    font-size: 18px !important;
  }

  .verify-code-row {
    .verify-code {
      width: 90px !important;
      font-size: 13px !important;
      letter-spacing: 2px !important;
    }
  }
}
</style>
