<template>
  <div ref="chartRef" :style="{ height: height + 'px', width: '100%' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'PieChart',
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
        console.log('饼图：无数据')
        return
      }
      
      if (!this.data.xAxis || !this.data.series) {
        console.log('饼图：缺少 xAxis 或 series 配置', this.data)
        return
      }
      
      console.log('饼图数据：', this.data)
      
      const seriesData = this.data.data.map(item => ({
        name: item[this.data.xAxis],
        value: item[this.data.series]
      }))
      
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
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'middle'
        },
        series: [{
          type: 'pie',
          radius: '50%',
          data: seriesData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          label: {
            formatter: '{b}: {d}%'
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
