// 模拟数据 - 基于原始报告数据生成

// 逐日数据（2026 年 3 月 1 日 -31 日）
export const mockDailyData = [
  { date: '2026-03-01', forecast: 850, actual: 820, accuracy: 96.4, mape: 3.6 },
  { date: '2026-03-02', forecast: 920, actual: 880, accuracy: 95.7, mape: 4.3 },
  { date: '2026-03-03', forecast: 780, actual: 850, accuracy: 91.8, mape: 8.2 },
  { date: '2026-03-04', forecast: 1050, actual: 980, accuracy: 93.3, mape: 6.7 },
  { date: '2026-03-05', forecast: 1200, actual: 1100, accuracy: 91.7, mape: 8.3 },
  { date: '2026-03-06', forecast: -4568, actual: 1150, accuracy: 0, mape: 100, abnormal: true },
  { date: '2026-03-07', forecast: -4568, actual: 1280, accuracy: 0, mape: 100, abnormal: true },
  { date: '2026-03-08', forecast: -4017, actual: 1050, accuracy: 0, mape: 100, abnormal: true },
  { date: '2026-03-09', forecast: 890, actual: 920, accuracy: 96.7, mape: 3.3 },
  { date: '2026-03-10', forecast: 750, actual: 800, accuracy: 93.8, mape: 6.2 },
  { date: '2026-03-11', forecast: 820, actual: 850, accuracy: 96.5, mape: 3.5 },
  { date: '2026-03-12', forecast: 950, actual: 900, accuracy: 94.7, mape: 5.3 },
  { date: '2026-03-13', forecast: 1100, actual: 1050, accuracy: 95.5, mape: 4.5 },
  { date: '2026-03-14', forecast: 1350, actual: 1200, accuracy: 88.9, mape: 11.1 },
  { date: '2026-03-15', forecast: 1280, actual: 1150, accuracy: 90.0, mape: 10.0 },
  { date: '2026-03-16', forecast: 880, actual: 950, accuracy: 92.6, mape: 7.4 },
  { date: '2026-03-17', forecast: 820, actual: 880, accuracy: 93.2, mape: 6.8 },
  { date: '2026-03-18', forecast: 900, actual: 850, accuracy: 94.4, mape: 5.6 },
  { date: '2026-03-19', forecast: 1050, actual: 1000, accuracy: 95.2, mape: 4.8 },
  { date: '2026-03-20', forecast: 1200, actual: 1100, accuracy: 91.7, mape: 8.3 },
  { date: '2026-03-21', forecast: 1400, actual: 1250, accuracy: 89.3, mape: 10.7 },
  { date: '2026-03-22', forecast: 1320, actual: 1200, accuracy: 90.9, mape: 9.1 },
  { date: '2026-03-23', forecast: 900, actual: 950, accuracy: 94.7, mape: 5.3 },
  { date: '2026-03-24', forecast: 850, actual: 900, accuracy: 94.4, mape: 5.6 },
  { date: '2026-03-25', forecast: 920, actual: 880, accuracy: 95.7, mape: 4.3 },
  { date: '2026-03-26', forecast: 1000, actual: 950, accuracy: 95.0, mape: 5.0 },
  { date: '2026-03-27', forecast: 1150, actual: 1050, accuracy: 91.3, mape: 8.7 },
  { date: '2026-03-28', forecast: 1380, actual: 1200, accuracy: 87.0, mape: 13.0 },
  { date: '2026-03-29', forecast: 1300, actual: 1180, accuracy: 90.8, mape: 9.2 },
  { date: '2026-03-30', forecast: 950, actual: 1000, accuracy: 95.0, mape: 5.0 },
  { date: '2026-03-31', forecast: 900, actual: 950, accuracy: 94.7, mape: 5.3 }
]

// 按星期维度数据
export const mockWeekdayData = [
  { weekday: '星期一', days: 5, forecastAvg: 920, actualAvg: 950, mae: 185, rmse: 220, bias: -30, accuracy: 93.5 },
  { weekday: '星期二', days: 5, forecastAvg: 880, actualAvg: 920, mae: 165, rmse: 195, bias: -40, accuracy: 94.2 },
  { weekday: '星期三', days: 5, forecastAvg: 950, actualAvg: 900, mae: 175, rmse: 210, bias: 50, accuracy: 93.8 },
  { weekday: '星期四', days: 5, forecastAvg: 1080, actualAvg: 1000, mae: 195, rmse: 235, bias: 80, accuracy: 92.5 },
  { weekday: '星期五', days: 5, forecastAvg: 1250, actualAvg: 1150, mae: 225, rmse: 275, bias: 100, accuracy: 90.8 },
  { weekday: '星期六', days: 4, forecastAvg: 1380, actualAvg: 1220, mae: 285, rmse: 340, bias: 160, accuracy: 87.5 },
  { weekday: '星期日', days: 4, forecastAvg: 1320, actualAvg: 1180, mae: 265, rmse: 315, bias: 140, accuracy: 88.6 }
]

// 误差分布数据
export const mockErrorDist = [
  { value: 0.0, name: '0-5%\n极准确', itemStyle: { color: '#67C23A' } },
  { value: 10.3, name: '5-10%\n准确', itemStyle: { color: '#409EFF' } },
  { value: 41.4, name: '10-20%\n尚可', itemStyle: { color: '#E6A23C' } },
  { value: 24.1, name: '20-30%\n偏差较大', itemStyle: { color: '#F56C6C' } },
  { value: 17.2, name: '30-50%\n偏差大', itemStyle: { color: '#F56C6C' } },
  { value: 6.9, name: '50% 以上\n严重偏差', itemStyle: { color: '#909399' } }
]

// 未来预估数据
export const mockFutureForecast = [
  { 
    date: '2026-04-01', 
    forecastSales: 850, 
    forecastRevenue: 12750, 
    historicalAvg: 896, 
    biasCorrection: -46 
  },
  { 
    date: '2026-04-02', 
    forecastSales: 920, 
    forecastRevenue: 13800, 
    historicalAvg: 849, 
    biasCorrection: 71 
  },
  { 
    date: '2026-04-03', 
    forecastSales: 1100, 
    forecastRevenue: 16500, 
    historicalAvg: 1035, 
    biasCorrection: 65 
  },
  { 
    date: '2026-04-04', 
    forecastSales: 1350, 
    forecastRevenue: 20250, 
    historicalAvg: 1346, 
    biasCorrection: 4 
  },
  { 
    date: '2026-04-05', 
    forecastSales: 1280, 
    forecastRevenue: 19200, 
    historicalAvg: 1371, 
    biasCorrection: -91 
  }
]

// 计算汇总数据
export const calculateMetrics = (dailyData) => {
  const normalData = dailyData.filter(d => !d.abnormal)
  
  // 计算 MAPE
  const totalMAPE = normalData.reduce((sum, d) => sum + d.mape, 0)
  const avgMAPE = totalMAPE / normalData.length
  
  // 计算准确率
  const avgAccuracy = normalData.reduce((sum, d) => sum + d.accuracy, 0) / normalData.length
  
  // 计算偏差
  const totalForecast = normalData.reduce((sum, d) => sum + d.forecast, 0)
  const totalActual = normalData.reduce((sum, d) => sum + d.actual, 0)
  const bias = (totalForecast - totalActual) / normalData.length
  
  return {
    accuracyRate: avgAccuracy.toFixed(1),
    salesMAPE: avgMAPE.toFixed(1),
    validDays: normalData.length,
    abnormalDays: dailyData.filter(d => d.abnormal).length,
    totalForecastSales: totalForecast,
    totalActualSales: totalActual,
    salesBias: bias.toFixed(1)
  }
}
