import request from './request'

/**
 * 用户登录
 * @param {Object} params - 登录参数 {eid, username, password, captcha}
 * @returns {Promise}
 */
export function userLogin(params) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'UserLogin',
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
 * 获取销售分析数据
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getSalesAnalysis(params) {
  return request({
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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
    url: '/service',
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

/**
 * 查询所有企业编号
 * @param {Object} params - 查询参数 {}
 * @returns {Promise}
 */
export function getAllEidQuery(params) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'AllEidQuery',
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
 * 商品销售明细查询
 * @param {Object} params - 查询参数 {shopId, startDate, endDate}
 * @returns {Promise}
 */
export function getDcpSaleQuery(params) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'DcpSaleQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      },
      pageNumber: params.pageNumber || 1,
      pageSize: params.pageSize || 20
    }
  })
}

/**
 * 品类销售分析查询
 * @param {Object} params - 查询参数 {shopId, startDate, endDate}
 * @returns {Promise}
 */
export function getCategorySaleQuery(params) {
  return request({
    url: '/service',
    method: 'post',
    data: {
      serviceId: 'CategorySaleQuery',
      request: params,
      sign: {
        key: 'digiwin',
        sign: '',
        token: ''
      },
      pageNumber: params.pageNumber || 1,
      pageSize: params.pageSize || 20
    }
  })
}
