<template>
  <div ref="chartRef" :style="{ height: height + 'px', width: '100%' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'LineChart',
  props: {
    data: Object,
    height: {
      type: Number,
      default: 300
    }
  },
  data() {
    return {
      chart: null,
      resizeObserver: null
    }
  },
  watch: {
    data: {
      handler() {
        this.updateChart()
      },
      deep: true
    }
  },
  mounted() {
    this.initChart()
    this.setupResizeObserver()
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
    }
    if (this.resizeObserver) {
      this.resizeObserver.disconnect()
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chartRef)
      this.updateChart()
    },
    setupResizeObserver() {
      this.resizeObserver = new ResizeObserver(() => {
        if (this.chart) {
          this.chart.resize()
        }
      })
      this.resizeObserver.observe(this.$refs.chartRef)
    },
    updateChart() {
      if (!this.data || !this.data.data || this.data.data.length === 0) {
        console.log('折线图：无数据')
        return
      }
      
      if (!this.data.xAxis || !this.data.series) {
        console.log('折线图：缺少 xAxis 或 series 配置', this.data)
        return
      }
      
      console.log('折线图数据：', this.data)
      
      const xAxisData = this.data.data.map(item => item[this.data.xAxis])
      const seriesData = this.data.data.map(item => item[this.data.series])
      
      const option = {
        title: {
          text: this.data.title,
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '60px',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData,
          boundaryGap: false
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          type: 'line',
          data: seriesData,
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#67C23A'
          },
          areaStyle: {
            color: 'rgba(103, 194, 58, 0.2)'
          },
          itemStyle: {
            color: '#67C23A'
          }
        }]
      }
      
      this.chart.setOption(option)
    }
  }
}
</script>

<style scoped>
</style>
