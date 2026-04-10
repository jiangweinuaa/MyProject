<template>
  <div class="api-doc">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">API 接口文档</span>
    </div>

    <!-- 接口列表 -->
    <el-row :gutter="20">
      <!-- 左侧：接口清单 -->
      <el-col :xs="24" :sm="8" :md="6">
        <el-card class="api-list-card" shadow="hover">
          <template #header>
            <div class="card-title">
              <el-icon><Menu /></el-icon>
              <span>接口清单</span>
            </div>
          </template>
          <el-menu :default-active="activeApi" @select="handleApiSelect">
            <el-menu-item 
              v-for="api in apiList" 
              :key="api.serviceId" 
              :index="api.serviceId"
            >
              <el-tag size="small" type="success" style="margin-right: 8px">
                {{ api.method }}
              </el-tag>
              {{ api.name }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧：接口详情 -->
      <el-col :xs="24" :sm="16" :md="18">
        <el-card class="api-detail-card" shadow="hover" v-loading="loading">
          <template #header>
            <div class="card-title">
              <el-icon><Document /></el-icon>
              <span>{{ apiDetail.name || '请选择接口' }}</span>
            </div>
          </template>

          <div v-if="apiDetail.serviceId" class="api-detail">
            <!-- 基本信息 -->
            <el-descriptions title="基本信息" :column="2" border>
              <el-descriptions-item label="接口 ID">
                <el-tag type="primary">{{ apiDetail.serviceId }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="请求方式">
                <el-tag :type="apiDetail.method === 'GET' ? 'success' : 'primary'">
                  {{ apiDetail.method }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="请求 URL" :span="2">
                <code>{{ apiDetail.url }}</code>
              </el-descriptions-item>
              <el-descriptions-item label="功能说明" :span="2">
                {{ apiDetail.description }}
              </el-descriptions-item>
            </el-descriptions>

            <!-- 请求字段 -->
            <el-divider content-position="left">
              <el-icon><Edit /></el-icon>
              请求字段
            </el-divider>
            <el-table :data="apiDetail.requestFields" style="width: 100%" size="small">
              <el-table-column prop="name" label="字段名" width="150">
                <template #default="{ row }">
                  <code>{{ row.name }}</code>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="required" label="必填" width="80" v-if="apiDetail.requestFields[0]?.required">
                <template #default="{ row }">
                  <el-tag :type="row.required === '是' ? 'danger' : 'info'" size="small">
                    {{ row.required }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="说明" />
              <el-table-column prop="remark" label="备注" v-if="apiDetail.requestFields[0]?.remark" />
            </el-table>

            <!-- 请求示例 -->
            <el-divider content-position="left">
              <el-icon><Connection /></el-icon>
              请求示例
            </el-divider>
            <pre class="code-block"><code>{{ apiDetail.requestExample }}</code></pre>

            <!-- 返回字段 -->
            <el-divider content-position="left">
              <el-icon><List /></el-icon>
              返回字段
            </el-divider>
            <el-table :data="apiDetail.responseFields" style="width: 100%" size="small">
              <el-table-column prop="name" label="字段名" width="200">
                <template #default="{ row }">
                  <code>{{ row.name }}</code>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="description" label="说明" />
            </el-table>

            <!-- 返回示例 -->
            <el-divider content-position="left">
              <el-icon><Finished /></el-icon>
              返回示例
            </el-divider>
            <pre class="code-block"><code>{{ apiDetail.responseExample }}</code></pre>
          </div>

          <el-empty v-else description="请从左侧选择接口" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const loading = ref(false)
const activeApi = ref('')
const apiList = ref([])
const apiDetail = reactive({})

// 获取接口列表
const loadApiList = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/api-query/list',
      method: 'get'
    })
    
    if (res.success && res.datas && res.datas.list) {
      apiList.value = res.datas.list
      // 默认选择第一个
      if (apiList.value.length > 0) {
        activeApi.value = apiList.value[0].serviceId
        loadApiDetail(activeApi.value)
      }
    }
  } catch (error) {
    console.error('加载接口列表失败:', error)
    ElMessage.error('加载接口列表失败')
  } finally {
    loading.value = false
  }
}

// 加载接口详情
const loadApiDetail = async (serviceId) => {
  loading.value = true
  try {
    const res = await request({
      url: '/api-query/detail',
      method: 'get',
      params: { serviceId }
    })
    
    if (res.success && res.datas) {
      Object.assign(apiDetail, res.datas)
    }
  } catch (error) {
    console.error('加载接口详情失败:', error)
    ElMessage.error('加载接口详情失败')
  } finally {
    loading.value = false
  }
}

// 选择接口
const handleApiSelect = (serviceId) => {
  activeApi.value = serviceId
  loadApiDetail(serviceId)
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadApiList()
})
</script>

<style scoped>
.api-doc {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  padding: 10px 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header .el-button {
  padding: 8px 15px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.api-list-card,
.api-detail-card {
  margin-bottom: 15px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.api-detail {
  margin-top: 10px;
}

code {
  background-color: #f4f4f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #e83e8c;
}

.code-block {
  background-color: #282c34;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 10px 0;
}

.code-block code {
  background-color: transparent;
  color: #abb2bf;
  padding: 0;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: 500;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #303133;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .page-header {
    padding: 10px;
  }

  .page-title {
    font-size: 16px;
  }

  :deep(.el-descriptions__label) {
    width: 80px;
  }
}
</style>
