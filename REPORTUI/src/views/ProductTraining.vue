<template>
  <div class="product-training">
    <!-- 页面标题 -->
    <div class="page-header">
      <span class="page-title">📸 商品训练</span>
      <el-tag v-if="isWeChat()" type="warning" size="small" style="margin-left: 10px">
        微信环境
      </el-tag>
    </div>
    
    <!-- 微信环境提示 -->
    <el-alert
      v-if="isWeChat()"
      title="微信环境提示"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 15px"
    >
      <template #default>
        <p>在微信中请使用"📸 选择图片"按钮拍照或选择图片</p>
        <p style="margin-top: 5px; font-size: 12px">如需使用摄像头实时预览，请在浏览器中打开此页面</p>
      </template>
    </el-alert>

    <!-- 1. 商品信息录入 -->
    <el-card class="info-card" shadow="hover">
      <template #header>
        <span>1️⃣ 商品信息</span>
      </template>
      
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品品号" required>
          <el-input v-model="form.pluno" placeholder="输入品号或扫码" clearable>
            <template #append>
              <el-button @click="scanBarcode">📷 扫码</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="商品名称" required>
          <el-input v-model="form.productName" placeholder="输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品类别">
          <el-select v-model="form.category" placeholder="选择类别" style="width: 100%">
            <el-option label="面包" value="面包" />
            <el-option label="蛋糕" value="蛋糕" />
            <el-option label="西点" value="西点" />
            <el-option label="饮品" value="饮品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 拍摄任务指引 -->
    <el-card class="task-card" shadow="hover" v-if="form.pluno">
      <template #header>
        <div class="task-header">
          <span>2️⃣ 拍摄建议</span>
          <el-progress :percentage="progressPercent" :status="isComplete ? 'success' : undefined" style="width: 200px" />
        </div>
      </template>
      
      <el-row :gutter="10">
        <el-col :xs="12" :sm="8" :md="6" v-for="(task, index) in shootingTasks" :key="index">
          <div class="task-item" :class="{ completed: task.completed }">
            <div class="task-icon">{{ task.icon }}</div>
            <div class="task-info">
              <div class="task-name">{{ task.name }}</div>
              <div class="task-progress">{{ task.current }}/{{ task.required }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-top: 15px"
      >
        <template #default>
          <p style="font-size: 13px">💡 建议：拍摄 5 张以上不同角度和光线的照片，可以提高识别准确率</p>
        </template>
      </el-alert>
    </el-card>

    <!-- 3. 已拍摄照片 -->
    <el-card class="images-card" shadow="hover" v-if="form.pluno">
      <template #header>
        <div class="card-header">
          <span>3️⃣ 已拍摄照片</span>
          <el-button 
            type="primary" 
            @click="showImagePicker" 
            :icon="Plus"
            :loading="uploading"
            size="small"
          >
            {{ isWeChat() ? '选择图片' : '添加照片' }}
          </el-button>
        </div>
      </template>
      
      <!-- 已拍摄图片网格 -->
      <div v-if="capturedImages.length > 0" class="image-grid">
        <div class="image-item" v-for="(img, index) in capturedImages" :key="index">
          <el-image 
            :src="img.url" 
            fit="cover"
            class="training-image"
            :preview-src-list="[img.url]"
          />
          <div class="image-overlay">
            <span class="image-task">{{ img.taskName }}</span>
            <el-button 
              type="danger" 
              size="small" 
              circle
              @click="deleteImage(index)"
              class="delete-btn"
            >
              ✕
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="点击加号添加照片" :image-size="80">
        <el-button type="primary" @click="showImagePicker" :icon="Plus">
          {{ isWeChat() ? '选择图片' : '添加照片' }}
        </el-button>
      </el-empty>
      
      <!-- 提交按钮 -->
      <div class="submit-section" v-if="capturedImages.length > 0">
        <el-alert
          v-if="capturedImages.length > 0 && capturedImages.length < 5"
          title="温馨提示"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 15px"
        >
          <template #default>
            <p>已拍摄 {{ capturedImages.length }} 张照片</p>
            <p style="margin-top: 5px; font-size: 12px; color: #909399">
              💡 建议：拍摄 5 张以上不同角度的照片可以提高识别准确率
            </p>
          </template>
        </el-alert>
        
        <el-button 
          type="success" 
          @click="submitImages" 
          :loading="uploading"
          :disabled="!canSubmit"
          block
          size="large"
        >
          <span v-if="!uploading">📤 提交 {{ capturedImages.length }} 张照片到训练库</span>
          <span v-else>提交中...</span>
        </el-button>
      </div>
    </el-card>
    
    <!-- 选择照片方式对话框 -->
    <el-dialog v-model="showChooseDialog" title="📸 选择照片方式" width="80%" top="40vh">
      <el-space direction="vertical" style="width: 100%">
        <el-button type="primary" @click="openCamera" block size="large">
          📷 拍照
        </el-button>
        <el-button @click="openAlbum" block size="large">
          🖼️ 从相册选择
        </el-button>
      </el-space>
    </el-dialog>
    
    <!-- 隐藏的文件输入框（用于拍照） -->
    <input 
      type="file" 
      ref="cameraInput" 
      id="cameraInput"
      accept="image/*" 
      capture="environment"
      style="position: fixed; top: -9999px; left: -9999px; opacity: 0;"
      @change="handleFileSelect"
    />
    
    <!-- 隐藏的文件输入框（用于相册） -->
    <input 
      type="file" 
      ref="albumInput" 
      id="albumInput"
      accept="image/*"
      multiple
      style="position: fixed; top: -9999px; left: -9999px; opacity: 0;"
      @change="handleFileSelect"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElImage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'

// 加载 vConsole（移动端调试工具）
onMounted(() => {
  if (typeof window !== 'undefined' && !window.VConsole) {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/vconsole@latest/dist/vconsole.min.js'
    script.onload = () => {
      if (window.VConsole) {
        new window.VConsole()
        console.log('vConsole 调试工具已加载，点击右下角按钮查看日志')
      }
    }
    document.head.appendChild(script)
  }
})

const form = reactive({
  pluno: '',
  productName: '',
  category: ''
})

// 拍摄任务配置
const shootingTasks = ref([
  { icon: '📐', name: '正面拍摄', required: 3, current: 0, completed: false },
  { icon: '📐', name: '侧面拍摄', required: 2, current: 0, completed: false },
  { icon: '📐', name: '45 度角', required: 2, current: 0, completed: false },
  { icon: '☀️', name: '自然光', required: 2, current: 0, completed: false },
  { icon: '💡', name: '室内光', required: 2, current: 0, completed: false },
  { icon: '🎨', name: '白色背景', required: 2, current: 0, completed: false },
  { icon: '🏪', name: '货架背景', required: 1, current: 0, completed: false },
  { icon: '📏', name: '近距离 (10cm)', required: 2, current: 0, completed: false },
  { icon: '📏', name: '中距离 (30cm)', required: 2, current: 0, completed: false }
])

const progressPercent = computed(() => {
  const total = shootingTasks.value.reduce((sum, t) => sum + t.required, 0)
  const current = shootingTasks.value.reduce((sum, t) => sum + t.current, 0)
  return Math.round((current / total) * 100)
})

const isComplete = computed(() => progressPercent.value >= 100)

const capturedImages = ref([])
const canSubmit = computed(() => {
  return form.pluno && form.productName && capturedImages.value.length >= 1
})

const cameraInputRef = ref(null)
const albumInputRef = ref(null)
const uploading = ref(false)
const showChooseDialog = ref(false)

// 判断是否在微信浏览器中
const isWeChat = () => {
  const ua = navigator.userAgent.toLowerCase()
  return ua.includes('micromessenger')
}

// 显示图片选择器
const showImagePicker = () => {
  showChooseDialog.value = true
}

// 打开相机
const openCamera = () => {
  showChooseDialog.value = false
  const cameraInput = cameraInputRef.value
  if (cameraInput) {
    cameraInput.click()
  } else {
    const input = document.getElementById('cameraInput')
    if (input) input.click()
  }
}

// 打开相册
const openAlbum = () => {
  showChooseDialog.value = false
  const albumInput = albumInputRef.value
  if (albumInput) {
    albumInput.click()
  } else {
    const input = document.getElementById('albumInput')
    if (input) input.click()
  }
}

// 处理文件选择
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  // 添加到已拍摄列表
  const imageUrl = URL.createObjectURL(file)
  const task = shootingTasks.value.find(t => !t.completed)
  
  capturedImages.value.push({
    url: imageUrl,
    file: file,
    taskName: task ? task.name : '未分类',
    taskIndex: shootingTasks.value.indexOf(task)
  })
  
  // 更新任务进度
  if (task) {
    task.current++
    if (task.current >= task.required) {
      task.completed = true
    }
  }
  
  ElMessage.success('图片已选择')
  
  // 清空 input
  event.target.value = ''
}

// 删除图片
const deleteImage = (index) => {
  const img = capturedImages.value[index]
  if (img.taskIndex !== undefined) {
    shootingTasks.value[img.taskIndex].current--
    shootingTasks.value[img.taskIndex].completed = false
  }
  capturedImages.value.splice(index, 1)
  ElMessage.success('已删除')
}

// 清空图片
const clearImages = () => {
  capturedImages.value = []
  shootingTasks.value.forEach(t => {
    t.current = 0
    t.completed = false
  })
}

// 提交训练数据
const submitImages = async () => {
  uploading.value = true
  
  console.log('=== 开始提交训练数据 ===')
  console.log('商品信息:', form.pluno, form.productName, form.category)
  console.log('图片数量:', capturedImages.value.length)
  
  try {
    const imageCount = capturedImages.value.length
    
    for (let i = 0; i < capturedImages.value.length; i++) {
      const img = capturedImages.value[i]
      
      console.log(`处理第 ${i + 1} 张图片...`)
      
      const formData = new FormData()
      formData.append('image', img.file || img.blob)
      formData.append('pluno', form.pluno)
      formData.append('productName', form.productName)
      formData.append('category', form.category || '')
      formData.append('metadata', JSON.stringify({
        taskName: img.taskName,
        timestamp: new Date().toISOString()
      }))
      
      console.log('请求地址:', 'http://47.100.138.89:8110/api/product/submit-training')
      console.log('FormData:', formData)
      
      // 使用 fetch 直接提交
      const response = await fetch('http://47.100.138.89:8110/api/product/submit-training', {
        method: 'POST',
        body: formData
      })
      
      console.log('响应状态:', response.status)
      console.log('响应头:', response.headers)
      
      if (!response.ok) {
        const errorText = await response.text()
        console.error('错误响应:', errorText)
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }
      
      const data = await response.json()
      console.log('响应数据:', data)
      
      if (!data.success) {
        throw new Error(data.message || '提交失败')
      }
    }
    
    // 提交成功
    console.log('=== 提交成功 ===')
    ElMessage({
      message: `✅ 成功提交 ${imageCount} 张照片到训练库！`,
      type: 'success',
      duration: 3000
    })
    
    // 清空表单
    clearImages()
    form.pluno = ''
    form.productName = ''
    form.category = ''
    
  } catch (error) {
    console.error('=== 提交失败 ===')
    console.error('错误信息:', error)
    console.error('错误堆栈:', error.stack)
    ElMessage({
      message: '❌ 提交失败：' + error.message,
      type: 'error',
      duration: 5000
    })
  } finally {
    uploading.value = false
  }
}

// 扫码
const scanBarcode = async () => {
  ElMessage.info('扫码功能开发中...')
}
</script>

<style scoped>
.product-training {
  padding: 15px;
}

.page-header {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.info-card,
.task-card,
.images-card {
  margin-bottom: 15px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: all 0.3s;
  background: #fff;
}

.task-item.completed {
  background: #f0f9ff;
  border-color: #409EFF;
}

.task-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.task-info {
  text-align: center;
}

.task-name {
  font-size: 13px;
  color: #303133;
  margin-bottom: 4px;
}

.task-progress {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #EBEEF5;
}

.training-image {
  width: 100%;
  height: 100%;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.image-task {
  color: white;
  font-size: 12px;
  text-align: center;
}

.delete-btn {
  align-self: flex-end;
}

.submit-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.submit-section :deep(.el-button) {
  font-size: 16px;
  font-weight: 500;
}

.submit-section :deep(.el-empty) {
  padding: 20px 0;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .image-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .task-item {
    padding: 10px 5px;
  }
  
  .task-icon {
    font-size: 24px;
  }
  
  .task-name {
    font-size: 12px;
  }
}
</style>
