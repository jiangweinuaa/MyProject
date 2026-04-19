<template>
  <div class="report-designer">
    <!-- 顶部工具栏 -->
    <div class="designer-header">
      <div class="header-left">
        <h1>📊 AI 报表设计器</h1>
        <el-tag v-if="currentStep" size="small" type="info">
          步骤 {{ currentStep }}/4
        </el-tag>
      </div>
      <div class="header-actions">
        <el-button @click="loadReportListAndShow">📂 编辑已有报表</el-button>
        <el-button @click="createNewReport">➕ 新增报表</el-button>
        <el-button @click="saveDraft" :loading="saving" v-if="currentStep === 4">💾 保存草稿</el-button>
        <el-button type="primary" @click="publish" :loading="publishing" v-if="currentStep === 4">🚀 发布</el-button>
      </div>
    </div>

    <!-- 主工作区（两栏布局） -->
    <div class="designer-body">
      <!-- 左侧 70%：实时预览区 -->
      <div class="preview-panel">
        <!-- 步骤 1：SQL 生成与数据预览（合并描述需求 + SQL 生成） -->
        <SQLEditor 
          v-if="currentStep === 1" 
          :sql="reportConfig.sql"
          :data="previewData"
          :chartConfig="reportConfig.chartConfig"
          @update="handleSQLUpdate"
          @execute="handleExecuteSQL"
          @regenerate="handleRegenerateSQL"
          @confirm="handleStepConfirm"
          @back="handleStepBack"
        />

        <!-- 步骤 2：图表设计与预览（合并图表选择 + 样式调整 + 布局设置） -->
        <ChartDesigner 
          v-else-if="currentStep === 2" 
          :data="previewData"
          :chartConfig="reportConfig.chartConfig"
          @select="handleChartSelect"
          @back="handleStepBack"
        />

        <!-- 步骤 3：添加筛选 -->
        <FilterForm 
          v-else-if="currentStep === 3" 
          :variables="reportConfig.variables"
          @update="handleFilterUpdate"
          @back="handleStepBack"
        />

        <!-- 步骤 4：保存报表（命名 + 选择目录） -->
        <SaveForm 
          ref="saveForm"
          v-else-if="currentStep === 4" 
          :report="reportConfig"
          @update="handleReportUpdate"
          @back="handleStepBack"
          @save="handleSaveFromForm"
          @publish="handlePublishFromForm"
        />
        
        <!-- 调试信息 -->
        <div v-if="currentStep === 4" style="margin-top: 10px; padding: 10px; background: #fff3cd; border-radius: 4px; font-size: 12px;">
          <div>当前步骤：{{ currentStep }}/4</div>
          <div>是否有 SQL: {{ !!reportConfig.sql }}</div>
          <div>SQL 长度：{{ reportConfig.sql?.length || 0 }}</div>
        </div>
      </div>

      <!-- 右侧 30%：AI 对话区 -->
      <ChatPanel 
        ref="chatPanel"
        @send="handleChat"
        @action="handleChatAction"
      />
    </div>
    
    <!-- 加载报表对话框 -->
    <el-dialog v-model="showLoadDialog" title="📂 编辑已有报表" width="700px">
      <el-table :data="reportList" style="width: 100%" @row-click="loadReport" stripe>
        <el-table-column prop="REPORT_NAME" label="报表名称" />
        <el-table-column prop="DESCRIPTION" label="描述" show-overflow-tooltip />
        <el-table-column prop="STATUS" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.STATUS === 200 ? 'success' : 'info'" size="small">
              {{ row.STATUS === 200 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="CREATED_TIME" label="创建时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click.stop="editReport(row.REPORT_ID)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click.stop="deleteReport(row.REPORT_ID)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ChatPanel from '@/components/Designer/ChatPanel.vue'
import SQLEditor from '@/components/Designer/SQLEditor.vue'
import DataTable from '@/components/Designer/DataTable.vue'
import ChartDesigner from '@/components/Designer/ChartDesigner.vue'
import FilterForm from '@/components/Designer/FilterForm.vue'
import SaveForm from '@/components/Designer/SaveForm.vue'
import { chat, saveReport, getReportList, getReportDetail, deleteReport as apiDeleteReport } from '@/api/designer'

const chatPanel = ref(null)
const saveForm = ref(null)
const currentStep = ref(1)
const saving = ref(false)
const publishing = ref(false)
const showLoadDialog = ref(false)
const reportList = ref([])

const previewData = ref(null)

const reportConfig = reactive({
  sql: '',
  chartConfig: null,
  variables: [],
  layout: null,
  name: '',
  description: '',
  menuId: ''
})

// 初始化会话
onMounted(() => {
  initSession()
})

async function initSession() {
  // 创建或恢复设计会话
  const sessionId = sessionStorage.getItem('designer_session_id')
  if (!sessionId) {
    const newSessionId = 'DESIGN_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    sessionStorage.setItem('designer_session_id', newSessionId)
  }
}

// 处理对话
async function handleChat(message) {
  try {
    const sessionId = sessionStorage.getItem('designer_session_id')
    const token = localStorage.getItem('token')
    
    // 先添加"思考中"消息
    if (chatPanel.value) {
      chatPanel.value.addAssistantMessage('🤖 思考中...', [])
    }
    
    const response = await chat({
      sessionId,
      question: message,
      token
    })
    
    if (response.success) {
      console.log('handleChat response:', response)
      
      // 更新预览数据
      previewData.value = {
        sql: response.sql,
        data: Array.isArray(response.data) ? response.data : [], // 确保是数组
        chartConfig: response.chartConfig,
        question: response.question,
        rowCount: response.rowCount || (Array.isArray(response.data) ? response.data.length : 0),
        execTime: response.execTime || 0
      }
      
      console.log('previewData:', previewData.value)
      console.log('response.sql:', response.sql)
      
      // 更新配置
      reportConfig.sql = response.sql
      console.log('reportConfig.sql after update:', reportConfig.sql)
      
      // 保存所有图表配置（可能包含多个图表）
      if (response.chartConfig) {
        reportConfig.chartConfig = response.chartConfig
      }
      if (response.charts && Array.isArray(response.charts)) {
        reportConfig.charts = response.charts
      }
      
      // 更新最后一条消息为成功（替换"思考中..."）
      if (chatPanel.value && chatPanel.value.messages.length > 0) {
        const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
        if (lastMsg.type === 'assistant' && lastMsg.content.includes('思考中')) {
          lastMsg.content = `✅ 已理解需求！正在生成 SQL...<br/>共 ${response.rowCount || 0} 条数据`
          lastMsg.actions = [
            { label: '👍 确认', type: 'success', action: 'confirm' },
            { label: '👎 重新生成', type: '', action: 'regenerate' }
          ]
        }
      }
      
      // 不再自动跳转，等待用户手动点击确认
    } else {
      ElMessage.error(response.message || '对话失败')
      // 更新最后一条消息为错误
      if (chatPanel.value && chatPanel.value.messages.length > 0) {
        const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
        if (lastMsg.type === 'assistant' && lastMsg.content.includes('思考中')) {
          lastMsg.content = `❌ ${response.message}`
        }
      }
    }
  } catch (error) {
    console.error('Chat error:', error)
    ElMessage.error('对话失败：' + error.message)
    // 更新最后一条消息为错误
    if (chatPanel.value && chatPanel.value.messages.length > 0) {
      const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
      if (lastMsg.type === 'assistant' && lastMsg.content.includes('思考中')) {
        lastMsg.content = `❌ 对话失败：${error.message}`
      }
    }
  }
}

// 处理对话快捷操作
function handleChatAction(action) {
  switch (action.type) {
    case 'regenerate':
      // 重新生成
      handleChat(previewData.value.question)
      break
    case 'edit':
      // 手动编辑
      currentStep.value = 2
      break
    case 'execute':
      // 执行 SQL
      handleExecuteSQL()
      break
  }
}

// 自动推进步骤
function autoAdvance() {
  if (currentStep.value < 9) {
    setTimeout(() => {
      currentStep.value++
    }, 500)
  }
}

// 步骤确认
function handleStepConfirm(data) {
  // 最后一步（步骤 4）不自动跳转
  if (currentStep.value < 4) {
    autoAdvance()
  }
}

// 上一步
function handleStepBack() {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

// 步骤编辑
function handleStepEdit(data) {
  handleStepBack()
}

// SQL 更新
function handleSQLUpdate(sql) {
  reportConfig.sql = sql
}

// 执行 SQL
async function handleExecuteSQL() {
  try {
    // TODO: 调用后端执行 SQL
    ElMessage.success('数据已更新')
    autoAdvance()
  } catch (error) {
    ElMessage.error('执行失败：' + error.message)
  }
}

// 重新生成 SQL（用 SQL 调用 AI 聊天功能）
async function handleRegenerateSQL(sql) {
  try {
    const sessionId = sessionStorage.getItem('designer_session_id')
    const token = localStorage.getItem('token')
    
    // 先添加"思考中"消息
    if (chatPanel.value) {
      chatPanel.value.addAssistantMessage('🔄 正在重新生成...', [])
    }
    
    // 用 SQL 调用 AI 聊天功能（后端检测到 SELECT 开头会直接执行）
    const response = await chat({
      sessionId,
      question: sql,  // 直接传 SQL
      token
    })
    
    if (response.success) {
      // 更新预览数据
      previewData.value = {
        sql: response.sql,
        data: Array.isArray(response.data) ? response.data : [],
        chartConfig: response.chartConfig,
        question: response.question,
        rowCount: response.rowCount || (Array.isArray(response.data) ? response.data.length : 0),
        execTime: response.execTime || 0
      }
      
      // 更新配置
      reportConfig.sql = response.sql
      if (response.chartConfig) {
        reportConfig.chartConfig = response.chartConfig
      }
      
      // 更新最后一条消息为成功（替换"正在重新生成..."）
      if (chatPanel.value && chatPanel.value.messages.length > 0) {
        const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
        if (lastMsg.type === 'assistant' && lastMsg.content.includes('正在重新生成')) {
          lastMsg.content = `✅ 重新生成成功！共 ${response.rowCount || 0} 条数据`
          lastMsg.actions = [
            { label: '👍 确认', type: 'success', action: 'confirm' },
            { label: '👎 重新生成', type: '', action: 'regenerate' }
          ]
        }
      }
      
      ElMessage.success('数据已更新')
    } else {
      ElMessage.error(response.message || '重新生成失败')
      // 更新最后一条消息为错误
      if (chatPanel.value && chatPanel.value.messages.length > 0) {
        const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
        if (lastMsg.type === 'assistant' && lastMsg.content.includes('正在重新生成')) {
          lastMsg.content = `❌ ${response.message}`
        }
      }
    }
  } catch (error) {
    console.error('重新生成失败:', error)
    ElMessage.error('重新生成失败：' + error.message)
    // 更新最后一条消息为错误
    if (chatPanel.value && chatPanel.value.messages.length > 0) {
      const lastMsg = chatPanel.value.messages[chatPanel.value.messages.length - 1]
      if (lastMsg.type === 'assistant' && lastMsg.content.includes('正在重新生成')) {
        lastMsg.content = `❌ 重新生成失败：${error.message}`
      }
    }
  }
}

// 图表选择
function handleChartSelect(result) {
  reportConfig.chartType = result.chartType
  reportConfig.chartConfig = result.chartConfig
  reportConfig.charts = result.charts || []  // 保存所有图表
  reportConfig.layout = result.layout
  
  console.log('选择的图表配置:', {
    chartType: reportConfig.chartType,
    chartConfig: reportConfig.chartConfig,
    charts: reportConfig.charts,
    layout: reportConfig.layout
  })
  
  autoAdvance()
}

// 样式更新
function handleStyleUpdate(styleConfig) {
  reportConfig.chartConfig = {
    ...reportConfig.chartConfig,
    ...styleConfig
  }
  autoAdvance()
}

// 筛选更新
function handleFilterUpdate(variables) {
  reportConfig.variables = variables
  autoAdvance()
}

// 布局更新
function handleLayoutUpdate(layout) {
  reportConfig.layout = layout
  autoAdvance()
}

// 报表信息更新
function handleReportUpdate(report) {
  reportConfig.name = report.name
  reportConfig.description = report.description
  reportConfig.menuId = report.menuId
  // 完成所有步骤
  ElMessage.success('报表已保存！')
}

// 手动更新报表信息（从表单）
function updateReportFromForm(form) {
  reportConfig.name = form.name
  reportConfig.description = form.description
  reportConfig.menuId = form.menuId
}

// 从子组件触发保存
async function handleSaveFromForm(formData) {
  saving.value = true
  try {
    console.log('=== 保存开始 ===')
    console.log('currentStep:', currentStep.value)
    console.log('formData:', formData)
    console.log('reportConfig:', JSON.stringify(reportConfig, null, 2))
    console.log('reportConfig.sql:', reportConfig.sql)
    console.log('reportConfig.chartConfig:', reportConfig.chartConfig)
    
    updateReportFromForm(formData)
    
    // 确保有 SQL 字段
    if (!reportConfig.sql || reportConfig.sql.trim() === '') {
      console.error('SQL 为空！用户可能没有完成步骤 1')
      ElMessage.warning('请先在步骤 1 生成 SQL 后再保存')
      saving.value = false
      return
    }
    
    const sessionId = sessionStorage.getItem('designer_session_id')
    const token = localStorage.getItem('token')
    
    // 保存所有图表配置（chartConfig 或 charts 数组）
    const saveData = {
      sessionId,
      name: formData.name || reportConfig.name,
      description: formData.description || reportConfig.description || '',
      sql: reportConfig.sql,
      chartConfig: reportConfig.chartConfig || null,  // 单个图表
      charts: reportConfig.charts || null,  // 多个图表
      layout: reportConfig.layout || null,
      variables: reportConfig.variables || [],
      menuId: formData.menuId || reportConfig.menuId || '',
      status: 100, // 草稿
      token
    }
    
    console.log('保存草稿数据:', saveData)
    
    const result = await saveReport(saveData)
    
    console.log('保存结果:', result)
    ElMessage.success('草稿已保存')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saving.value = false
  }
}

// 从子组件触发发布
async function handlePublishFromForm(formData) {
  try {
    // 确保有 SQL 字段
    if (!reportConfig.sql) {
      ElMessage.warning('没有可保存的 SQL，请先生成 SQL')
      return
    }
    
    await ElMessageBox.confirm(
      '确定要发布此报表吗？发布后其他用户将可以看到。',
      '确认发布',
      { type: 'warning' }
    )
    
    updateReportFromForm(formData)
    
    publishing.value = true
    const sessionId = sessionStorage.getItem('designer_session_id')
    const token = localStorage.getItem('token')
    
    const saveData = {
      sessionId,
      name: formData.name || reportConfig.name,
      description: formData.description || reportConfig.description || '',
      sql: reportConfig.sql,
      chartConfig: reportConfig.chartConfig || null,
      layout: reportConfig.layout || null,
      variables: reportConfig.variables || [],
      menuId: formData.menuId || reportConfig.menuId || '',
      status: 200, // 已发布
      token
    }
    
    console.log('发布数据:', saveData)
    
    const result = await saveReport(saveData)
    
    console.log('发布结果:', result)
    ElMessage.success('报表已发布')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
      ElMessage.error('发布失败：' + error.message)
    }
  } finally {
    publishing.value = false
  }
}

// 新增报表
function createNewReport() {
  // 重置所有状态
  currentStep.value = 1
  reportConfig.sql = ''
  reportConfig.chartConfig = null
  reportConfig.layout = null
  reportConfig.variables = []
  reportConfig.name = ''
  reportConfig.description = ''
  reportConfig.menuId = ''
  previewData.value = null
  
  // 清空对话
  if (chatPanel.value) {
    chatPanel.value.messages = []
  }
  
  ElMessage.success('已创建新报表')
}

// 显示报表列表并获取数据
async function loadReportListAndShow() {
  try {
    const response = await getReportList()
    if (response.success) {
      reportList.value = response.reports || []
      console.log('报表列表:', reportList.value)
    } else {
      ElMessage.error('获取报表列表失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取报表列表失败:', error)
    ElMessage.error('获取报表列表失败：' + error.message)
  }
  showLoadDialog.value = true
}

// 显示报表列表（不获取数据）
async function loadReportList() {
  try {
    const response = await getReportList()
    if (response.success) {
      reportList.value = response.reports || []
    }
  } catch (error) {
    console.error('获取报表列表失败:', error)
  }
}

// 编辑报表
async function editReport(reportId) {
  try {
    console.log('加载报表:', reportId)
    const response = await getReportDetail(reportId)
    console.log('报表详情:', response)
    
    if (response.success) {
      const report = response.report
      
      // 加载报表数据（注意：后端返回的是大写字段名）
      reportConfig.sql = report.SQL_TEXT || ''
      reportConfig.name = report.REPORT_NAME || ''
      reportConfig.description = report.DESCRIPTION || ''
      
      // 安全解析 JSON 字段
      const parsedChartConfig = safeParseJSON(report.CHART_CONFIG, null)
      
      // 处理图表配置：可能是单个对象或数组
      if (Array.isArray(parsedChartConfig)) {
        // 多图表数组：取第一个作为主图表，保存完整数组
        reportConfig.charts = parsedChartConfig
        // 从第一个图表提取 config
        const firstChart = parsedChartConfig[0]
        reportConfig.chartConfig = firstChart?.config || firstChart
        reportConfig.chartType = firstChart?.type || 'bar'
        console.log('加载多图表配置:', reportConfig.charts)
      } else {
        // 单个图表对象
        reportConfig.chartConfig = parsedChartConfig
        reportConfig.charts = null
      }
      
      reportConfig.layout = safeParseJSON(report.LAYOUT_CONFIG, null)
      reportConfig.variables = safeParseJSON(report.VARIABLES_CONFIG, [])
      reportConfig.menuId = report.MENU_ID || ''
      
      console.log('加载的报表配置:', reportConfig)
      
      // 进入步骤 1（SQL 编辑）
      currentStep.value = 1
      
      ElMessage.success('报表已加载：' + report.REPORT_NAME)
      showLoadDialog.value = false
    } else {
      ElMessage.error('加载报表失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('加载报表失败:', error)
    ElMessage.error('加载报表失败：' + error.message)
  }
}

// 安全解析 JSON
function safeParseJSON(str, defaultValue) {
  if (!str) return defaultValue
  try {
    const parsed = JSON.parse(str)
    return parsed
  } catch (e) {
    console.warn('JSON 解析失败，使用默认值:', str, e.message)
    // 如果是简单字符串，直接返回
    if (typeof str === 'string' && !str.startsWith('{') && !str.startsWith('[')) {
      return str
    }
    return defaultValue
  }
}

// 删除报表
async function deleteReport(reportId) {
  try {
    const response = await apiDeleteReport(reportId)
    if (response.success) {
      ElMessage.success('报表已删除')
      // 刷新列表
      loadReportList()
    }
  } catch (error) {
    console.error('删除报表失败:', error)
    ElMessage.error('删除失败：' + error.message)
  }
}

// 加载报表（点击行）
function loadReport(row) {
  editReport(row.REPORT_ID)
}

// 顶部按钮的保存（兼容旧代码）
async function saveDraft() {
  if (saveForm.value) {
    // 如果在步骤 4，触发子组件的保存
    saveForm.value.handleSave()
  }
}

async function publish() {
  if (saveForm.value) {
    // 如果在步骤 4，触发子组件的发布
    saveForm.value.handlePublishClick()
  }
}
</script>

<style scoped lang="scss">
.report-designer {
  display: flex;
  flex-direction: column;
  height: 100%; // 占满父容器高度，而不是视口高度
  background: #f5f7fa;
}

.designer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
    
    h1 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #303133;
    }
  }
  
  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.designer-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.preview-panel {
  flex: 0 0 70%;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}

.chat-panel {
  flex: 0 0 30%;
  border-left: 1px solid #e4e7ed;
  background: white;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

// 移动端适配
@media screen and (max-width: 768px) {
  .designer-header {
    padding: 10px 15px;
    
    .header-left {
      h1 {
        font-size: 15px;
      }
    }
    
    .header-actions {
      :deep(.el-button) {
        padding: 8px 10px;
        font-size: 12px;
      }
    }
  }
  
  .designer-body {
    flex-direction: column;
  }
  
  .preview-panel {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
  }
  
  .chat-panel {
    flex: none;
    height: 40%;
    border-left: none;
    border-top: 1px solid #e4e7ed;
  }
}

// 小屏幕手机
@media screen and (max-width: 480px) {
  .designer-header {
    .header-actions {
      :deep(.el-button) {
        padding: 6px 8px;
        font-size: 11px;
      }
    }
  }
  
  .chat-panel {
    height: 45%;
  }
}
</style>
