<template>
  <div class="ai-chart-container">
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
  created() {
    console.log('🎨 AiChart: created 钩子被调用')
    console.log('🎨 AiChart: props.config =', this.config)
  },
  watch: {
    config: {
      handler(newConfig) {
        console.log('🎨 AiChart: watch 检测到 config 变化')
        this.renderChart(newConfig)
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    console.log('🎨 AiChart: mounted 钩子被调用')
    console.log('🎨 AiChart: $refs.chartContainer =', this.$refs.chartContainer)
    
    this.$nextTick(() => {
      console.log('🎨 AiChart: nextTick 回调执行')
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
      console.log('📊 AiChart: initChart 被调用')
      console.log('📊 AiChart: chartContainer DOM 存在吗？', !!this.$refs.chartContainer)
      console.log('📊 AiChart: config 是什么？', this.config)
      
      if (!this.$refs.chartContainer) {
        console.error('❌ AiChart: chartContainer DOM 不存在！')
        return
      }
      
      try {
        // 初始化图表
        this.chart = echarts.init(this.$refs.chartContainer)
        console.log('📊 AiChart: 图表实例已创建', !!this.chart, this.chart)
        
        // 渲染图表
        this.renderChart(this.config)
      } catch (error) {
        console.error('❌ AiChart: echarts.init 失败:', error)
      }
    },
    
    renderChart(config) {
      console.log('📊 AiChart: renderChart 被调用')
      console.log('📊 AiChart: this.chart 存在吗？', !!this.chart)
      
      if (!this.chart) {
        console.error('❌ AiChart: chart 实例不存在！')
        return
      }
      
      if (!config) {
        console.error('❌ AiChart: config 为空！')
        return
      }
      
      try {
        console.log('📊 AiChart: 设置配置项...')
        // 设置配置项
        this.chart.setOption(config, true)
        console.log('📊 AiChart: 配置项设置成功！')
        
        // 添加点击事件
        this.chart.on('click', (params) => {
          console.log('图表点击:', params)
        })
      } catch (error) {
        console.error('❌ AI 图表渲染失败:', error)
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
