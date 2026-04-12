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

    <!-- 识别结果 - 多商品列表 -->
    <el-card class="result-card" shadow="hover" v-if="recognitionResult">
      <template #header>
        <div class="card-header">
          <span>✅ 识别结果</span>
          <el-tag type="success">
            共识别 {{ recognitionResult.products?.length || 0 }} 种商品
          </el-tag>
        </div>
      </template>
      
      <!-- 识别信息 -->
      <el-alert
        :title="recognitionResult.message || '识别成功'"
        type="success"
        :closable="false"
        show-icon
        style="margin-bottom: 15px"
      />
      
      <!-- 多商品列表 -->
      <div class="product-list" v-if="recognitionResult.products && recognitionResult.products.length > 0">
        <el-card 
          v-for="(product, index) in recognitionResult.products" 
          :key="index"
          class="product-item"
          shadow="hover"
        >
          <div class="product-item-header">
            <span class="product-index">商品 {{ index + 1 }}</span>
            <el-tag :type="product.confidence > 0.8 ? 'success' : (product.confidence > 0.5 ? 'warning' : 'danger')">
              置信度：{{ Math.round(product.confidence * 100) }}%
            </el-tag>
          </div>
          
          <el-descriptions :column="1" border size="small">
            <!-- 临时品号（识别结果）+ 匹配来源 -->
            <el-descriptions-item label="识别结果">
              <div class="pluno-row">
                <span class="temp-pluno">{{ product.sourcePluno || '未知商品' }}</span>
                <el-tag size="small" :type="product.hasRealPluno ? 'success' : 'warning'">
                  {{ product.hasRealPluno ? '已匹配' : '未匹配' }}
                </el-tag>
                <el-tag size="small" type="info" v-if="product.matchSource === 'ALIYUN'">
                  阿里云识别
                </el-tag>
              </div>
            </el-descriptions-item>
            
            <!-- 真实品号（匹配结果）+ 修改按钮 -->
            <el-descriptions-item label="真实品号">
              <div class="pluno-row">
                <div class="real-pluno-edit">
                  <el-input 
                    v-if="product.editing" 
                    v-model="product.pluno" 
                    size="small"
                    placeholder="品号"
                    style="width: 100px; margin-right: 5px"
                  />
                  <span v-else class="real-pluno">{{ product.pluno || '无' }}</span>
                  <el-button 
                    v-if="product.editing" 
                    type="success" 
                    size="small" 
                    @click="savePlunoEdit(product)"
                  >
                    确认
                  </el-button>
                  <el-button 
                    v-else 
                    type="primary" 
                    size="small" 
                    @click="editPluno(product)"
                  >
                    {{ product.hasRealPluno ? '修改' : '填写' }}
                  </el-button>
                </div>
              </div>
            </el-descriptions-item>
            
            <el-descriptions-item label="商品名称">
              {{ product.productName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="商品类别">
              {{ product.category || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="识别数量">
              {{ product.quantity || 1 }}
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 提交到训练库 -->
          <div class="submit-training-action" v-if="product.pluno">
            <el-button 
              type="warning" 
              size="small" 
              @click="submitToTraining(product)"
              :loading="product.submitting"
              :disabled="!product.hasRealPluno"
            >
              📚 提交到训练库
            </el-button>
            <span class="submit-hint" v-if="!product.hasRealPluno">（需先修改真实品号）</span>
            <span class="submit-hint" v-if="product.submitted">（已提交）</span>
          </div>
        </el-card>
      </div>
      
      <!-- 重新识别 -->
      <div class="confirm-actions">
        <el-button @click="retakePhoto" type="warning" :icon="Refresh">
          🔄 重新识别
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

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
      // 为每个商品添加编辑状态
      if (recognitionResult.value.products) {
        recognitionResult.value.products.forEach(product => {
          product.editing = false
          product.submitting = false
          product.submitted = false
          // 标记是否有真实品号（后端返回的 pluno 不为空即表示有真实品号）
          product.hasRealPluno = !!product.pluno
        })
      }
      const productCount = response.datas.products?.length || response.productCount || 1
      ElMessage.success(response.message || `识别成功，共识别到 ${productCount} 种商品`)
    } else {
      ElMessage.error('识别失败：' + (response.message || '未找到匹配商品'))
    }
    
  } catch (error) {
    ElMessage.error('识别失败：' + error.message)
  } finally {
    recognizing.value = false
  }
}

// 编辑品号
const editPluno = (product) => {
  product.editing = true
}

// 保存品号修改
const savePlunoEdit = (product) => {
  product.editing = false
  // 用户手动填写/修改后，标记为有真实品号
  product.hasRealPluno = !!product.pluno
  ElMessage.success(product.hasRealPluno ? '真实品号已更新' : '已清空')
}

// 提交到训练库
const submitToTraining = async (product) => {
  // 没有真实品号不允许提交（临时品号不算）
  if (!product.hasRealPluno || !product.pluno) {
    ElMessage.warning('请先填写真实品号')
    return
  }
  
  product.submitting = true
  
  try {
    const formData = new FormData()
    formData.append('image', selectedImage.value.file)
    formData.append('pluno', product.pluno)
    formData.append('productName', product.productName || '')
    formData.append('category', product.category || '')
    
    const response = await fetch('http://47.100.138.89:8110/api/product/submit-training', {
      method: 'POST',
      body: formData
    }).then(res => res.json())
    
    if (response.success) {
      ElMessage.success('提交成功！已添加到训练库')
      // 标记为已提交
      product.submitted = true
    } else {
      ElMessage.error('提交失败：' + (response.message || '未知错误'))
    }
    
  } catch (error) {
    ElMessage.error('提交失败：' + error.message)
  } finally {
    product.submitting = false
  }
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

/* 多商品列表样式 */
.product-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-item {
  margin-bottom: 0;
}

.product-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.product-index {
  font-size: 14px;
  font-weight: 600;
  color: #409EFF;
}

/* 品号编辑样式 */
.pluno-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.temp-pluno {
  font-family: 'Courier New', monospace;
  font-size: 15px;
  color: #606266;
}

.real-pluno {
  font-family: 'Courier New', monospace;
  font-size: 15px;
  color: #409EFF;
  font-weight: 500;
}

.real-pluno-edit {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 提交到训练库样式 */
.submit-training-action {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ebeef5;
  display: flex;
  align-items: center;
  gap: 10px;
}

.submit-hint {
  font-size: 12px;
  color: #909399;
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
