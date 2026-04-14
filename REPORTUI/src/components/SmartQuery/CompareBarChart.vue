<template>
  <div ref="chartRef" :style="{ height: height + 'px', width: '100%' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'CompareBarChart',
  props: {
    data: Object,
    height: {
      type: Number,
      default: 200
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
        console.log('对比柱状图：无数据')
        return
      }
      
      const rowData = this.data.data[0]
      const columns = Object.keys(rowData)
      
      console.log('对比柱状图数据：', columns, rowData)
      
      // 过滤掉增长率列
      const valueColumns = columns.filter(col => {
        const colUpper = col.toUpperCase()
        return !colUpper.includes('GROWTH') && 
               !colUpper.includes('RATE') && 
               !colUpper.includes('RATIO') &&
               !colUpper.includes('占比')
      })
      
      // 查找增长率列
      const growthColumn = columns.find(col => {
        const colUpper = col.toUpperCase()
        return colUpper.includes('GROWTH') || 
               colUpper.includes('RATE') || 
               colUpper.includes('RATIO') ||
               colUpper.includes('占比')
      })
      
      const seriesData = valueColumns.map(col => rowData[col])
      const growthValue = growthColumn ? rowData[growthColumn] : null
      
      // 判断增长率正负
      const isGrowthUp = growthValue !== null && growthValue > 0
      
      const option = {
        title: {
          text: this.data.title || '数据对比',
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: '{b}: {c}'
        },
        grid: {
          left: '3%',
          right: '15%',
          bottom: '3%',
          top: '50px',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}'
          }
        },
        yAxis: {
          type: 'category',
          data: valueColumns.map(col => this.formatLabel(col)),
          axisLabel: {
            interval: 'auto',
            fontSize: 12
          }
        },
        series: [{
          type: 'bar',
          data: seriesData,
          barWidth: '50%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ])
          },
          label: {
            show: true,
            position: 'right',
            formatter: (params) => {
              return this.formatValue(params.value)
            },
            fontSize: 12,
            color: '#333'
          }
        }],
        // 添加增长率标注
        graphic: growthValue !== null ? [{
          type: 'text',
          right: '5%',
          top: 'center',
          style: {
            text: isGrowthUp ? `📈 +${this.formatValue(growthValue)}` : `📉 ${this.formatValue(growthValue)}`,
            font: 'bold 16px sans-serif',
            fill: isGrowthUp ? '#f56c6c' : '#67C23A'
          }
        }] : []
      }
      
      this.chart.setOption(option)
    },
    formatLabel(col) {
      // 简化列名显示
      const colUpper = col.toUpperCase()
      if (colUpper.includes('LAST')) return '上期'
      if (colUpper.includes('THIS') || colUpper.includes('CURR')) return '本期'
      if (colUpper.includes('PREV')) return '上期'
      if (colUpper.includes('MONTH')) return '月'
      if (colUpper.includes('YEAR')) return '年'
      if (colUpper.includes('SALES')) return '销售额'
      if (colUpper.includes('QTY')) return '数量'
      return col
    },
    formatValue(value) {
      if (typeof value === 'number') {
        // 判断是否是百分比
        if (value > -1 && value < 1 && value !== 0) {
          return (value * 100).toFixed(1) + '%'
        }
        // 大数字格式化
        if (value >= 10000) {
          return (value / 10000).toFixed(1) + '万'
        }
        return value.toLocaleString(undefined, {
          minimumFractionDigits: 0,
          maximumFractionDigits: 2
        })
      }
      return value
    }
  }
}
</script>

<style scoped>
</style>
