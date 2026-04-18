<template>
  <div ref="chartContainer" class="chart-wrapper" style="width: 100%; height: 400px;"></div>
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
      if (!this.$refs.chartContainer) {
        return
      }
      
      try {
        // 初始化图表
        this.chart = echarts.init(this.$refs.chartContainer)
        
        // 渲染图表
        this.renderChart(this.config)
      } catch (error) {
        console.error('❌ AiChart: echarts.init 失败:', error)
      }
    },
    
    renderChart(config) {
      if (!this.chart) {
        return
      }
      
      if (!config) {
        return
      }
      
      try {
        // 设置配置项
        this.chart.setOption(config, true)
        
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
.chart-wrapper {
  width: 100%;
  height: 400px;
}
</style>
