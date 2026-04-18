<template>
  <div class="ai-chart-container">
    <div class="chart-header">
      <el-tag type="success" size="small">🤖 AI 生成</el-tag>
    </div>
    <div ref="chartContainer" class="chart-wrapper"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'AiChart',
  props: {
    config: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    config: {
      handler(newConfig) {
        this.renderChart(newConfig)
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
    
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chart) {
      this.chart.dispose()
    }
  },
  methods: {
    initChart() {
      if (!this.$refs.chartContainer) return
      
      // 初始化图表
      this.chart = echarts.init(this.$refs.chartContainer)
      
      // 渲染图表
      this.renderChart(this.config)
    },
    
    renderChart(config) {
      if (!this.chart) return
      
      try {
        // 设置配置项
        this.chart.setOption(config, true)
        
        // 添加点击事件
        this.chart.on('click', (params) => {
          console.log('图表点击:', params)
        })
      } catch (error) {
        console.error('AI 图表渲染失败:', error)
        console.error('配置:', config)
      }
    },
    
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    }
  }
}
</script>

<style scoped>
.ai-chart-container {
  margin: 15px 0;
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-wrapper {
  width: 100%;
  height: 400px;
}
</style>
