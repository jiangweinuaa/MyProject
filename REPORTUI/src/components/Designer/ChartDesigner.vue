<template>
  <div class="chart-designer">
    <div class="card">
      <div class="card-header">
        <el-icon><Picture /></el-icon>
        <span>步骤 2/4: 图表设计与预览</span>
      </div>
      
      <div class="card-body">
        <el-row :gutter="20">
          <!-- 左侧：图表选择和样式调整 -->
          <el-col :span="10" class="config-panel">
            <!-- AI 推荐 -->
            <div v-if="aiRecommend" class="ai-recommend">
              <div class="recommend-title">
                <el-icon><Star /></el-icon>
                <span>AI 推荐：{{ aiRecommend.type }}</span>
              </div>
              <div class="recommend-reason">
                推荐理由：{{ aiRecommend.reason }}
              </div>
            </div>
            
            <!-- 图表类型选择 -->
            <div class="config-section">
              <div class="section-title">📊 图表类型</div>
              <div class="chart-types">
                <div 
                  v-for="chart in chartTypes" 
                  :key="chart.type"
                  :class="['chart-type-card', { active: selectedType === chart.type }]"
                  @click="selectChart(chart.type)"
                >
                  <div class="chart-icon">
                    <component :is="chart.icon" />
                  </div>
                  <div class="chart-name">{{ chart.name }}</div>
                </div>
              </div>
            </div>
            
            <!-- 颜色方案 -->
            <div class="config-section">
              <div class="section-title">🎨 颜色方案</div>
              <div class="color-schemes">
                <div 
                  v-for="scheme in colorSchemes" 
                  :key="scheme.name"
                  :class="['scheme-card', { active: selectedScheme === scheme.name }]"
                  @click="selectScheme(scheme.name)"
                >
                  <div class="scheme-colors">
                    <span 
                      v-for="color in scheme.colors" 
                      :key="color"
                      :style="{ background: color }"
                      class="color-item"
                    />
                  </div>
                  <div class="scheme-name">{{ scheme.name }}</div>
                </div>
              </div>
            </div>
            
            <!-- 显示选项 -->
            <div class="config-section">
              <div class="section-title">📝 显示选项</div>
              <el-checkbox v-model="options.showTitle">显示标题</el-checkbox>
              <el-checkbox v-model="options.showLegend">显示图例</el-checkbox>
              <el-checkbox v-model="options.showLabel">显示数据标签</el-checkbox>
              <el-checkbox v-model="options.showGrid">显示网格线</el-checkbox>
            </div>
            
            <!-- 标题设置 -->
            <div class="config-section">
              <div class="section-title">📝 图表标题</div>
              <el-input 
                v-model="options.title" 
                placeholder="输入图表标题"
                clearable
              />
            </div>
          </el-col>
          
          <!-- 右侧：实时预览 + 布局设置 -->
          <el-col :span="14" class="preview-panel">
            <div class="preview-header">
              <div class="preview-title">👁️ 实时预览</div>
              
              <!-- 布局模板选择 -->
              <div class="layout-templates">
                <div class="section-title">📐 布局模板</div>
                <div class="layout-options">
                  <div 
                    v-for="layout in layoutTemplates" 
                    :key="layout.type"
                    :class="['layout-card', { active: selectedLayout === layout.type }]"
                    @click="selectLayout(layout.type)"
                  >
                    <div class="layout-preview">
                      <div 
                        v-for="area in layout.areas" 
                        :key="area"
                        :class="['area', area]"
                      />
                    </div>
                    <div class="layout-name">{{ layout.name }}</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="preview-content">
              <AiChart 
                v-if="previewConfig" 
                :config="previewConfig" 
                :autoresize="true"
              />
            </div>
          </el-col>
        </el-row>
        
        <!-- 操作按钮 -->
        <div class="actions">
          <el-button @click="$emit('back')">⬅️ 上一步</el-button>
          <el-button @click="handleChange">🎨 换一种</el-button>
          <el-button type="success" @click="handleConfirm">✅ 确认，下一步</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { Picture, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AiChart from '../SmartQuery/AiChart.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  chartConfig: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['select', 'back'])

const selectedType = ref('bar')
const selectedScheme = ref('经典蓝')
const selectedLayout = ref('single')
const previewConfig = ref(props.chartConfig)

const options = reactive({
  showTitle: true,
  showLegend: true,
  showLabel: false,
  showGrid: true,
  title: ''
})

const layoutTemplates = [
  {
    type: 'single',
    name: '单图表',
    areas: ['chart']
  },
  {
    type: 'vertical',
    name: '上下双图',
    areas: ['chart1', 'chart2']
  },
  {
    type: 'horizontal',
    name: '左右双图',
    areas: ['chart1', 'chart2']
  },
  {
    type: 'dashboard',
    name: '仪表盘',
    areas: ['metric1', 'metric2', 'metric3', 'chart']
  }
]

function selectLayout(type) {
  selectedLayout.value = type
}

const chartTypes = [
  { type: 'bar', name: '柱状图', icon: 'Histogram' },
  { type: 'line', name: '折线图', icon: 'TrendCharts' },
  { type: 'pie', name: '饼图', icon: 'PieChart' },
  { type: 'horizontalBar', name: '条形图', icon: 'BarChart' },
  { type: 'area', name: '面积图', icon: 'AreaChart' },
  { type: 'metric', name: '指标卡', icon: 'Grid' }
]

const colorSchemes = [
  {
    name: '经典蓝',
    colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
  },
  {
    name: '活力橙',
    colors: ['#ff6b6b', '#feca57', '#48dbfb', '#ff9ff3', '#54a0ff']
  },
  {
    name: '清新绿',
    colors: ['#26de81', '#20bf6b', '#0fb9b1', '#45aaf2', '#a55eea']
  },
  {
    name: '商务灰',
    colors: ['#2d3436', '#636e72', '#b2bec3', '#dfe6e9', '#fdcb6e']
  }
]

const aiRecommend = computed(() => {
  if (!props.data || !props.data.rows) return null
  
  const rowCount = props.data.rows?.length || 0
  const columnCount = Object.keys(props.data.rows[0] || {}).length || 0
  
  if (columnCount === 2) {
    return {
      type: '柱状图',
      reason: '适合展示分类数据的对比'
    }
  } else if (columnCount === 3) {
    return {
      type: '折线图',
      reason: '适合展示时间序列趋势'
    }
  }
  
  return {
    type: '柱状图',
    reason: '通用图表类型，适合大多数场景'
  }
})

const previewConfigComputed = computed(() => {
  if (!props.chartConfig && !props.data) return null
  
  // 根据选择的图表类型和样式生成配置
  return {
    ...props.chartConfig,
    title: {
      show: options.showTitle,
      text: options.title || props.chartConfig?.title?.text || '数据图表'
    },
    legend: {
      show: options.showLegend
    },
    series: props.chartConfig?.series?.map(s => ({
      ...s,
      type: getEChartsType(selectedType.value),
      label: {
        show: options.showLabel
      }
    })) || [],
    color: getColorScheme(selectedScheme.value)
  }
})

function getEChartsType(type) {
  const typeMap = {
    bar: 'bar',
    line: 'line',
    pie: 'pie',
    horizontalBar: 'bar',
    area: 'line',
    metric: 'bar'
  }
  return typeMap[type] || 'bar'
}

function selectChart(type) {
  selectedType.value = type
}

function selectScheme(name) {
  selectedScheme.value = name
}

function getColorScheme(name) {
  const scheme = colorSchemes.find(s => s.name === name)
  return scheme ? scheme.colors : colorSchemes[0].colors
}

function handleChange() {
  const types = chartTypes.map(t => t.type)
  const currentIndex = types.indexOf(selectedType.value)
  const nextIndex = (currentIndex + 1) % types.length
  selectChart(types[nextIndex])
}

function handleConfirm() {
  // 生成所有图表配置（根据布局模板）
  const allCharts = generateAllCharts()
  
  emit('select', {
    chartType: selectedType.value,
    chartConfig: previewConfigComputed.value,  // 主图表
    charts: allCharts,  // 所有图表
    layout: selectedLayout.value
  })
}

// 生成所有图表配置
function generateAllCharts() {
  const charts = []
  
  // 根据布局生成不同的图表
  if (selectedLayout.value === 'single') {
    // 单图表布局：只有柱图
    charts.push({
      type: 'bar',
      config: previewConfigComputed.value
    })
  } else if (selectedLayout.value === 'vertical' || selectedLayout.value === 'horizontal') {
    // 双图布局：柱图 + 饼图
    charts.push({
      type: 'bar',
      config: previewConfigComputed.value
    })
    charts.push({
      type: 'pie',
      config: generatePieConfig()
    })
  } else if (selectedLayout.value === 'dashboard') {
    // 仪表盘：多个图表
    charts.push({
      type: 'metric',
      config: generateMetricConfig()
    })
    charts.push({
      type: 'bar',
      config: previewConfigComputed.value
    })
    charts.push({
      type: 'pie',
      config: generatePieConfig()
    })
  }
  
  return charts
}

// 生成饼图配置
function generatePieConfig() {
  if (!props.data?.rows) return null
  
  const rows = props.data.rows
  if (rows.length === 0) return null
  
  const columns = Object.keys(rows[0])
  if (columns.length < 2) return null
  
  const categoryColumn = columns[0]
  const valueColumn = columns[1]
  
  return {
    title: { text: '占比分析', show: options.showTitle },
    tooltip: { trigger: 'item' },
    legend: { show: options.showLegend, orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: rows.map(row => ({
        name: row[categoryColumn],
        value: Number(row[valueColumn]) || 0
      })),
      label: { show: options.showLabel },
      color: getColorScheme(selectedScheme.value)
    }]
  }
}

// 生成指标卡配置
function generateMetricConfig() {
  if (!props.data?.rows) return null
  
  const rows = props.data.rows
  if (rows.length === 0) return null
  
  const columns = Object.keys(rows[0])
  
  return {
    title: { text: '数据指标', show: options.showTitle },
    series: columns.map((col, i) => ({
      type: 'gauge',
      radius: '80%',
      center: [(25 + (i % 2) * 50) + '%', (25 + Math.floor(i / 2) * 50) + '%'],
      progress: { show: true },
      detail: { valueAnimation: true, formatter: '{value}' },
      data: [{ value: 0, name: col }]
    }))
  }
}
</script>

<style scoped lang="scss">
.chart-designer {
  .card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 15px 20px;
    border-bottom: 1px solid #e4e7ed;
    font-weight: 600;
    color: #303133;
  }
  
  .card-body {
    padding: 20px;
  }
  
  .config-panel {
    max-height: 600px;
    overflow-y: auto;
  }
  
  .ai-recommend {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    
    .recommend-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
      margin-bottom: 5px;
    }
    
    .recommend-reason {
      font-size: 13px;
      opacity: 0.9;
    }
  }
  
  .config-section {
    margin-bottom: 25px;
    
    .section-title {
      font-weight: 600;
      margin-bottom: 12px;
      color: #303133;
    }
  }
  
  .chart-types {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    
    .chart-type-card {
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      padding: 12px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409EFF;
        background: #f5f7fa;
      }
      
      &.active {
        border-color: #409EFF;
        background: #ecf5ff;
      }
      
      .chart-icon {
        height: 40px;
        margin-bottom: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: #606266;
      }
      
      .chart-name {
        font-size: 13px;
        color: #606266;
      }
    }
  }
  
  .color-schemes {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    
    .scheme-card {
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      padding: 12px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409EFF;
      }
      
      &.active {
        border-color: #409EFF;
        background: #ecf5ff;
      }
      
      .scheme-colors {
        display: flex;
        gap: 4px;
        margin-bottom: 8px;
        
        .color-item {
          flex: 1;
          height: 25px;
          border-radius: 4px;
        }
      }
      
      .scheme-name {
        text-align: center;
        font-size: 13px;
        color: #606266;
      }
    }
  }
  
  .preview-panel {
    background: #f5f7fa;
    border-radius: 8px;
    padding: 15px;
    height: 600px;
    display: flex;
    flex-direction: column;
    
    .preview-header {
      margin-bottom: 15px;
      
      .preview-title {
        font-weight: 600;
        margin-bottom: 10px;
        color: #606266;
      }
    }
    
    .preview-content {
      flex: 1;
      background: white;
      border-radius: 8px;
      padding: 15px;
      overflow: hidden;
    }
  }
  
  .layout-templates {
    margin-bottom: 15px;
    
    .section-title {
      font-weight: 600;
      margin-bottom: 12px;
      color: #303133;
      font-size: 14px;
    }
    
    .layout-options {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 10px;
    }
    
    .layout-card {
      border: 2px solid #e4e7ed;
      border-radius: 6px;
      padding: 10px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409EFF;
        background: #f5f7fa;
      }
      
      &.active {
        border-color: #409EFF;
        background: #ecf5ff;
      }
      
      .layout-preview {
        height: 50px;
        background: #fff;
        border-radius: 4px;
        margin-bottom: 8px;
        display: flex;
        flex-direction: column;
        gap: 3px;
        padding: 5px;
        
        .area {
          background: #c0c4cc;
          border-radius: 2px;
          
          &.chart {
            flex: 1;
          }
          
          &.chart1, &.chart2 {
            flex: 1;
          }
          
          &.metric1, &.metric2, &.metric3 {
            height: 12px;
          }
        }
      }
      
      .layout-name {
        text-align: center;
        font-size: 12px;
        color: #606266;
      }
    }
  }
  
  .actions {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    margin-top: 20px;
    flex-wrap: wrap;
    
    .el-button {
      flex: 0 0 auto;
    }
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .card-body {
    padding: 15px;
  }
  
  .config-panel {
    max-height: none;
  }
  
  .preview-panel {
    height: 350px;
  }
  
  .actions {
    gap: 8px;
    
    .el-button {
      flex: 1 1 calc(50% - 4px);
      justify-content: center;
    }
  }
}

@media screen and (max-width: 480px) {
  .preview-panel {
    height: 300px;
  }
  
  .actions {
    .el-button {
      flex: 1 1 100%;
      font-size: 13px;
      padding: 10px 15px;
    }
  }
}
</style>
