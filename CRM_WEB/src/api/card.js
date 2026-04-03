import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 查询所有卡片
export function queryAllCards() {
  return api.get('/card/queryAll')
}

// 根据卡号查询卡片
export function queryCard(cardNo) {
  return api.post('/card/query', { cardNo })
}

export default api
