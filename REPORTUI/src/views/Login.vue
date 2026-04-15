<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">🦞</div>
        <h1 class="title">REPORT 报表系统</h1>
        <p class="subtitle">用户登录</p>
      </div>

      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form" size="large">
        <el-form-item prop="eid">
          <el-select
            v-model="loginForm.eid"
            placeholder="请选择企业编号"
            prefix-icon="OfficeBuilding"
            clearable
            filterable
            style="width: 100%"
            @focus="loadEidList"
          >
            <el-option
              v-for="eid in eidList"
              :key="eid"
              :label="eid"
              :value="eid"
            />
          </el-select>
        </el-form-item>

        <el-form-item prop="opno">
          <el-input
            v-model="loginForm.opno"
            placeholder="操作员编号"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #append>
              <span style="font-size: 12px; color: #909399; padding: 0 10px;">初始密码 000</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="captcha">
          <div class="captcha-input">
            <el-input
              v-model="loginForm.captcha"
              placeholder="验证码"
              prefix-icon="Key"
              clearable
              @keyup.enter="handleLogin"
            />
            <div class="captcha-image" @click="refreshCaptcha">
              <span class="captcha-text">{{ captchaText }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
            size="large"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>© 2026 REPORT System. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userLogin, getAllEidQuery } from '@/api/report'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const captchaText = ref('ABCD')

// 登录表单 - 四要素
const loginForm = reactive({
  eid: '',
  opno: 'admin',
  password: '',
  captcha: ''
})

// 企业编号列表
const eidList = ref([])

// 加载企业编号列表
const loadEidList = async () => {
  if (eidList.value.length > 0) return  // 已加载过
  
  try {
    const response = await getAllEidQuery({})
    if (response.success && response.datas && response.datas.list) {
      eidList.value = response.datas.list
    }
  } catch (error) {
    console.error('加载企业编号失败:', error)
  }
}

// 表单验证规则
const loginRules = {
  eid: [
    { required: true, message: '请输入企业编号', trigger: 'blur' }
  ],
  opno: [
    { required: true, message: '请输入操作员编号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 生成随机验证码
const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  captchaText.value = result
}

// 刷新验证码
const refreshCaptcha = () => {
  generateCaptcha()
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 调用后端登录接口
        const response = await userLogin({
          eid: loginForm.eid,
          username: loginForm.opno,
          password: loginForm.password,
          captcha: loginForm.captcha
        })
        
        if (response.success && response.datas) {
          // 保存 token 到 localStorage
          localStorage.setItem('token', response.datas.token)
          localStorage.setItem('userInfo', JSON.stringify(response.datas))
          
          ElMessage.success('登录成功')
          
          // 使用 router.push 跳转（跳转到智问首页）
          router.push('/smart-query')
        } else {
          ElMessage.error(response.serviceDescription || '登录失败')
          refreshCaptcha()
          loginForm.captcha = ''
        }
        
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败：' + error.message)
        refreshCaptcha()
        loginForm.captcha = ''
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  generateCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 40px;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  font-size: 64px;
  margin-bottom: 10px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 10px 0;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 20px;
}

.captcha-input {
  display: flex;
  gap: 10px;
}

.captcha-input :deep(.el-input) {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  background: linear-gradient(45deg, #f0f0f0, #e0e0e0);
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  user-select: none;
  border: 1px solid #dcdfe6;
}

.captcha-text {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  letter-spacing: 3px;
  font-family: 'Courier New', monospace;
}

.captcha-image:hover {
  background: linear-gradient(45deg, #e0e0e0, #d0d0d0);
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }

  .logo {
    font-size: 48px;
  }

  .title {
    font-size: 20px;
  }
}
</style>
