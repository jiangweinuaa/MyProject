import request from './request'

/**
 * 获取销售分析数据
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getSalesAnalysis(params) {
  return request({
    url: '/service/GetSalesAnalysis',
    method: 'post',
    data: {
      serviceId: 'GetSalesAnalysis',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 获取销售趋势数据
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getSalesTrend(params) {
  return request({
    url: '/service/GetSalesTrend',
    method: 'post',
    data: {
      serviceId: 'GetSalesTrend',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 获取商品销售排行
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getProductRanking(params) {
  return request({
    url: '/service/GetProductRanking',
    method: 'post',
    data: {
      serviceId: 'GetProductRanking',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 每日销售查询
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise}
 */
export function getDaySaleQuery(params) {
  return request({
    url: '/service/DaySaleQuery',
    method: 'post',
    data: {
      serviceId: 'DaySaleQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 每日门店商品查询
 * @param {Object} params - 查询参数 {startDate, endDate, shopId}
 * @returns {Promise}
 */
export function getDayShopGoodsQuery(params) {
  return request({
    url: '/service/DayShopGoodsQuery',
    method: 'post',
    data: {
      serviceId: 'DayShopGoodsQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 每日渠道查询
 * @param {Object} params - 查询参数 {startDate, endDate}
 * @returns {Promise}
 */
export function getDayChannelQuery(params) {
  return request({
    url: '/service/DayChannelQuery',
    method: 'post',
    data: {
      serviceId: 'DayChannelQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 每日门店渠道查询
 * @param {Object} params - 查询参数 {startDate, endDate, shopId}
 * @returns {Promise}
 */
export function getDayShopChannelQuery(params) {
  return request({
    url: '/service/DayShopChannelQuery',
    method: 'post',
    data: {
      serviceId: 'DayShopChannelQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 商品库存查询
 * @param {Object} params - 查询参数 {shopId}
 * @returns {Promise}
 */
export function getStockQuery(params) {
  return request({
    url: '/service/StockQuery',
    method: 'post',
    data: {
      serviceId: 'StockQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}

/**
 * 门店库存汇总查询
 * @param {Object} params - 查询参数 {}
 * @returns {Promise}
 */
export function getStockSumQuery(params) {
  return request({
    url: '/service/StockSumQuery',
    method: 'post',
    data: {
      serviceId: 'StockSumQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      }
    }
  })
}
