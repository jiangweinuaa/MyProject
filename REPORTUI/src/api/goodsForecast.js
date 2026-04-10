import request from './request'

/**
 * 查询单品准确性分析完整数据
 */
export function queryGoodsAccuracyAnalysisFull(data) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'ShopGoodsSaleForcastQuery',
      request: data
    }
  })
}
