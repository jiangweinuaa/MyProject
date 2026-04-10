<template>
  <div class="product-recognition">
    <!-- 页面标题 -->
    <div class="page-header">
      <span class="page-title">🔍 商品识别</span>
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
        <p>在微信中可以使用拍照或相册选择图片进行识别</p>
      </template>
    </el-alert>

    <!-- 摄像头/图片预览区域 -->
    <el-card class="camera-card" shadow="hover" v-if="!isWeChat() && !selectedImage">
      <template #header>
        <span>📷 摄像头预览</span>
      </template>
      
      <div class="camera-view">
        <video ref="videoRef" autoplay playsinline class="video-element"></video>
        <canvas ref="canvasRef" style="display:none"></canvas>
        
        <!-- 拍摄引导框 -->
        <div class="guide-overlay">
          <div class="corner top-left"></div>
          <div class="corner top-right"></div>
          <div class="corner bottom-left"></div>
          <div class="corner bottom-right"></div>
        </div>
      </div>
      
      <!-- 控制按钮 -->
      <div class="camera-controls">
        <el-button 
          @click="showImagePicker" 
          type="primary" 
          size="large" 
          round 
          :loading="recognizing"
        >
          {{ recognizing ? '识别中...' : '📸 拍照/选择图片' }}
        </el-button>
      </div>
    </el-card>
    
    <!-- 微信环境下的按钮 -->
    <el-card class="camera-card" shadow="hover" v-else-if="!selectedImage">
      <template #header>
        <span>📸 选择图片识别</span>
      </template>
      
      <div class="wechat-hint">
        <el-button 
          @click="showImagePicker" 
          type="primary" 
          size="large" 
          round 
          block
          :loading="recognizing"
        >
          {{ recognizing ? '识别中...' : '📸 拍照/从相册选择' }}
        </el-button>
      </div>
    </el-card>
    
    <!-- 图片预览区域 -->
    <el-card class="preview-card" shadow="hover" v-if="selectedImage">
      <template #header>
        <div class="card-header">
          <span>📷 已选图片</span>
          <el-button type="danger" size="small" @click="clearImage">
            🗑️ 清除
          </el-button>
        </div>
      </template>
      
      <div class="preview-container">
        <el-image 
          :src="selectedImage.url" 
          fit="contain"
          class="preview-image"
          :preview-src-list="[selectedImage.url]"
        />
      </div>
      
      <!-- 识别按钮 -->
      <div class="recognize-action">
        <el-button 
          type="success" 
          @click="recognizeProduct(selectedImage.file)" 
          :loading="recognizing"
          block
          size="large"
        >
          {{ recognizing ? '🔍 识别中...' : '🚀 开始识别' }}
        </el-button>
      </div>
    </el-card>
    
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
      style="position: fixed; top: -9999px; left: -9999px; opacity: 0;"
      @change="handleFileSelect"
    />

    <!-- 识别结果 -->
    <el-card class="result-card" shadow="hover" v-if="recognitionResult">
      <template #header>
        <div class="card-header">
          <span>✅ 识别结果</span>
          <el-tag :type="recognitionResult.confidence > 0.8 ? 'success' : 'warning'">
            置信度：{{ Math.round(recognitionResult.confidence * 100) }}%
          </el-tag>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品品号">
          {{ recognitionResult.pluno }}
        </el-descriptions-item>
        <el-descriptions-item label="商品名称">
          {{ recognitionResult.productName }}
        </el-descriptions-item>
        <el-descriptions-item label="商品类别">
          {{ recognitionResult.category }}
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 用户确认 -->
      <div class="confirm-actions">
        <el-button @click="confirmResult" type="success" :icon="Check">
          ✓ 确认结果
        </el-button>
        <el-button @click="retakePhoto" type="warning" :icon="Refresh">
          🔄 重新识别
        </el-button>
      </div>
    </el-card>

    <!-- 相似商品列表 -->
    <el-card class="similar-card" shadow="hover" v-if="recognitionResult?.similarProducts?.length > 0">
      <template #header>
        <span>🔎 相似商品</span>
      </template>
      
      <el-table :data="recognitionResult.similarProducts" style="width: 100%">
        <el-table-column prop="pluno" label="品号" width="120" />
        <el-table-column prop="productName" label="名称" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column label="相似度" width="100">
          <template #default="{ row }">
            <el-tag :type="row.similarity > 0.8 ? 'success' : 'primary'">
              {{ Math.round(row.similarity * 100) }}%
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Refresh } from '@element-plus/icons-vue'

const videoRef = ref(null)
const canvasRef = ref(null)
const cameraInputRef = ref(null)
const albumInputRef = ref(null)
const stream = ref(null)
const recognizing = ref(false)
const recognitionResult = ref(null)
const selectedImage = ref(null)  // 新增：选中的图片

// 判断是否在微信浏览器中
const isWeChat = () => {
  const ua = navigator.userAgent.toLowerCase()
  return ua.includes('micromessenger')
}

// 显示图片选择器 - 直接触发相册选择
const showImagePicker = () => {
  openAlbum()
}

// 打开相机
const openCamera = () => {
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
  
  // 创建图片预览 URL
  const imageUrl = URL.createObjectURL(file)
  
  // 保存选中的图片
  selectedImage.value = {
    file: file,
    url: imageUrl
  }
  
  // 清空 input
  event.target.value = ''
  
  ElMessage.success('图片已选择，请点击"开始识别"')
}

// 识别商品
const recognizeProduct = async (imageBlob) => {
  recognizing.value = true
  
  try {
    const formData = new FormData()
    formData.append('image', imageBlob)
    
    const response = await fetch('http://47.100.138.89:8110/api/product/recognize', {
      method: 'POST',
      body: formData
    }).then(res => res.json())
    
    if (response.success && response.datas) {
      recognitionResult.value = response.datas
      ElMessage.success('识别成功')
    } else {
      ElMessage.error('识别失败：' + (response.message || '未找到匹配商品'))
    }
    
  } catch (error) {
    ElMessage.error('识别失败：' + error.message)
  } finally {
    recognizing.value = false
  }
}

// 确认结果
const confirmResult = () => {
  ElMessage.success('结果已确认')
}

// 重新识别
const retakePhoto = () => {
  recognitionResult.value = null
  clearImage()
}

// 清除图片
const clearImage = () => {
  if (selectedImage.value) {
    // 释放 URL 对象
    URL.revokeObjectURL(selectedImage.value.url)
    selectedImage.value = null
  }
  recognitionResult.value = null
}

// 启动摄像头（仅浏览器）
const startCamera = async () => {
  if (isWeChat()) return
  
  try {
    stream.value = await navigator.mediaDevices.getUserMedia({
      video: {
        facingMode: 'environment',
        width: { ideal: 1280 },
        height: { ideal: 720 }
      }
    })
    videoRef.value.srcObject = stream.value
  } catch (error) {
    console.error('摄像头启动失败:', error)
  }
}

// 组件卸载时关闭摄像头
onUnmounted(() => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop())
  }
})

// 初始化
onMounted(() => {
  if (!isWeChat()) {
    startCamera()
  }
})
</script>

<style scoped>
.product-recognition {
  padding: 10px;
  max-width: 600px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.camera-card,
.preview-card {
  margin-bottom: 10px;
}

.camera-view {
  position: relative;
  width: 100%;
  height: 300px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.guide-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 3px solid #409EFF;
}

.top-left {
  top: -2px;
  left: -2px;
  border-right: none;
  border-bottom: none;
}

.top-right {
  top: -2px;
  right: -2px;
  border-left: none;
  border-bottom: none;
}

.bottom-left {
  bottom: -2px;
  left: -2px;
  border-right: none;
  border-top: none;
}

.bottom-right {
  bottom: -2px;
  right: -2px;
  border-left: none;
  border-top: none;
}

.camera-controls {
  display: flex;
  justify-content: center;
  padding: 15px;
}

.wechat-hint {
  padding: 15px;
  text-align: center;
}

/* 图片预览样式 */
.preview-container {
  width: 100%;
  min-height: 250px;
  max-height: 400px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
}

.preview-image {
  max-width: 100%;
  max-height: 350px;
  border-radius: 8px;
}

.recognize-action {
  margin-top: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.confirm-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  justify-content: center;
}

.similar-card {
  margin-top: 10px;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .product-recognition {
    padding: 8px;
  }
  
  .camera-view {
    height: 250px;
  }
  
  .guide-overlay {
    width: 180px;
    height: 180px;
  }
  
  .preview-container {
    min-height: 200px;
    max-height: 350px;
  }
  
  .preview-image {
    max-height: 300px;
  }
}
</style>
