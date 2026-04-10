import request from './request'

/**
 * 查询准确性分析完整数据（一个接口返回所有数据）
 * 使用统一服务入口 /service
 */
export function queryAccuracyAnalysisFull(data) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'ShopSaleForecastQuery',
      request: data
    }
  })
}
