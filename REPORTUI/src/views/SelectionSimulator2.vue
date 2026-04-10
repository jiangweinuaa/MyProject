<template>
  <div class="meituan-simulator">
    <div class="main-container">
      <!-- 左侧分类栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <el-icon><Menu /></el-icon>
          <span>品类分类</span>
        </div>
        <div class="category-list">
          <div 
            v-for="cat in categories" 
            :key="cat.id"
            :class="['category-item', { active: selectedCategory === cat.id }]"
            @click="selectedCategory = cat.id"
          >
            <div class="category-icon">{{ cat.icon }}</div>
            <div class="category-name">{{ cat.name }}</div>
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 顶部搜索栏 -->
        <div class="search-header">
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商家或商品"
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
            />
            <el-button type="warning" @click="handleSearch">搜索</el-button>
          </div>
        </div>

        <!-- 排序筛选 -->
        <div class="sort-bar">
          <div class="sort-options">
            <div 
              :class="['sort-item', { active: sortType === 'default' }]"
              @click="sortType = 'default'"
            >
              默认
            </div>
            <div 
              :class="['sort-item', { active: sortType === 'sales' }]"
              @click="sortType = 'sales'"
            >
              销量优先
            </div>
            <div 
              :class="['sort-item', { active: sortType === 'rating' }]"
              @click="sortType = 'rating'"
            >
              评分优先
            </div>
            <div 
              :class="['sort-item', { active: sortType === 'distance' }]"
              @click="sortType = 'distance'"
            >
              距离最近
            </div>
          </div>
        </div>

        <!-- 商家列表 -->
        <div class="shop-list">
          <div 
            v-for="shop in filteredShops" 
            :key="shop.id"
            class="shop-item"
            @click="handleShopClick(shop)"
          >
            <div class="shop-left">
              <div class="shop-logo">
                <el-avatar :size="70" :src="shop.logo" shape="square" />
              </div>
              <div class="shop-info">
                <div class="shop-header">
                  <div class="shop-name">{{ shop.name }}</div>
                  <el-tag 
                    :type="shop.rating >= 4.5 ? 'success' : (shop.rating >= 4 ? 'primary' : 'warning')"
                    size="small"
                    class="rating-tag"
                  >
                    {{ shop.rating }}分
                  </el-tag>
                </div>
                <div class="shop-desc">{{ shop.description }}</div>
                <div class="shop-meta">
                  <span class="meta-item">
                    <el-icon><Location /></el-icon>
                    {{ shop.distance }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Timer /></el-icon>
                    {{ shop.deliveryTime }}
                  </span>
                  <span class="meta-item">
                    <el-icon><ShoppingBag /></el-icon>
                    月售{{ shop.monthlySales }}
                  </span>
                  <span class="meta-item">
                    起送¥{{ shop.minPrice }}
                  </span>
                </div>
              </div>
            </div>
            <div class="shop-right">
              <div class="promotion-badge">
                <span class="promotion-text">满减</span>
              </div>
              <div class="shop-actions">
                <el-button type="warning" size="small" round @click.stop="handleOrder(shop)">
                  点餐
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-if="filteredShops.length === 0" description="暂无商家" />
      </div>
    </div>

    <!-- 商家详情对话框 -->
    <el-dialog 
      v-model="shopDialogVisible" 
      :title="selectedShop?.name" 
      width="700px"
      class="shop-dialog"
    >
      <div class="shop-detail" v-if="selectedShop">
        <!-- 商家头图 -->
        <div class="detail-banner">
          <el-avatar :size="100" :src="selectedShop.logo" shape="square" />
          <div class="banner-info">
            <div class="banner-name">{{ selectedShop.name }}</div>
            <div class="banner-rating">
              <el-rate v-model="selectedShop.rating" disabled />
              <span>{{ selectedShop.rating }}分</span>
            </div>
            <div class="banner-meta">
              <span>{{ selectedShop.distance }}</span>
              <span>·</span>
              <span>{{ selectedShop.deliveryTime }}</span>
              <span>·</span>
              <span>月售{{ selectedShop.monthlySales }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 商家信息 -->
        <div class="detail-section">
          <div class="section-title">商家信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="入驻时间">{{ selectedShop.joinDate }}</el-descriptions-item>
            <el-descriptions-item label="商品数量">{{ selectedShop.productCount }}款</el-descriptions-item>
            <el-descriptions-item label="粉丝数量">{{ selectedShop.followerCount }}</el-descriptions-item>
            <el-descriptions-item label="起送价格">¥{{ selectedShop.minPrice }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 优惠活动 -->
        <div class="detail-section">
          <div class="section-title">
            <el-icon><Discount /></el-icon>
            优惠活动
          </div>
          <div class="promotion-list">
            <div class="promotion-item">
              <el-tag type="danger" size="small">满减</el-tag>
              <span>满¥50 减¥10，满¥100 减¥25</span>
            </div>
            <div class="promotion-item">
              <el-tag type="warning" size="small">折扣</el-tag>
              <span>指定商品 8.8 折</span>
            </div>
            <div class="promotion-item">
              <el-tag type="success" size="small">新人</el-tag>
              <span>新人专享¥20 红包</span>
            </div>
          </div>
        </div>

        <!-- 热门商品 -->
        <div class="detail-section">
          <div class="section-title">
            <el-icon><ShoppingCart /></el-icon>
            热门商品
          </div>
          <el-table :data="selectedShop.products" style="width: 100%">
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="price" label="价格" width="100" align="right">
              <template #default="{ row }">
                <span style="color: #F56C6C; font-weight: bold">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sales" label="月售" width="80" align="center" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="warning" size="small" round @click="handleAddToCart(row)">
                  加入购物车
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 商家标签 -->
        <div class="detail-section">
          <div class="section-title">特色标签</div>
          <div class="tags-container">
            <el-tag 
              v-for="tag in selectedShop.tags" 
              :key="tag" 
              effect="plain"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="shopDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleFollow">关注商家</el-button>
        <el-button type="warning" @click="handleOrder(selectedShop)">立即下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Location, Timer, ShoppingBag, ShoppingCart, Discount, Star, Menu } from '@element-plus/icons-vue'

const router = useRouter()

// 品类列表
const categories = [
  { id: 'all', name: '全部', icon: '🏪' },
  { id: 'cake', name: '蛋糕', icon: '🎂' },
  { id: 'bread', name: '面包', icon: '🍞' },
  { id: 'pastry', name: '糕点', icon: '🥐' },
  { id: 'cookie', name: '饼干', icon: '🍪' },
  { id: 'chocolate', name: '巧克力', icon: '🍫' },
  { id: 'icecream', name: '冰淇淋', icon: '🍦' }
]

// 商家数据（美团风格）
const shopsData = {
  all: [
    {
      id: 1,
      name: '巴黎贝甜',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=PB&backgroundColor=FFB6C1',
      description: '法式烘焙经典，传承百年工艺',
      rating: 4.8,
      distance: '1.2km',
      deliveryTime: '30 分钟',
      monthlySales: 8520,
      minPrice: 20,
      productCount: 156,
      followerCount: '12.5 万',
      joinDate: '2023-05-15',
      tags: ['法式烘焙', '高端定制', '生日蛋糕'],
      products: [
        { name: '草莓奶油蛋糕', price: '298', sales: 1250 },
        { name: '巧克力慕斯', price: '328', sales: 980 },
        { name: '芒果千层', price: '268', sales: 856 }
      ]
    },
    {
      id: 2,
      name: '好利来',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=HLL&backgroundColor=87CEEB',
      description: '国民烘焙品牌，品质值得信赖',
      rating: 4.6,
      distance: '800m',
      deliveryTime: '25 分钟',
      monthlySales: 15200,
      minPrice: 15,
      productCount: 203,
      followerCount: '28.3 万',
      joinDate: '2022-08-20',
      tags: ['国民品牌', '性价比高', '连锁门店'],
      products: [
        { name: '半熟芝士', price: '28', sales: 5200 },
        { name: '空气蛋糕', price: '168', sales: 3100 },
        { name: '蜂蜜蛋糕', price: '88', sales: 2800 }
      ]
    },
    {
      id: 3,
      name: '泸溪河',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=LXH&backgroundColor=FFE4B5',
      description: '中式糕点老字号',
      rating: 4.7,
      distance: '2.5km',
      deliveryTime: '40 分钟',
      monthlySales: 18500,
      minPrice: 18,
      productCount: 95,
      followerCount: '25.8 万',
      joinDate: '2022-07-08',
      tags: ['中式传统', '老字号', '现烤现卖'],
      products: [
        { name: '桃酥', price: '28', sales: 8500 },
        { name: '绿豆饼', price: '25', sales: 6200 },
        { name: '蛋黄酥', price: '35', sales: 5100 }
      ]
    },
    {
      id: 4,
      name: '墨茉点心局',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=MM&backgroundColor=FFC0CB',
      description: '新中式烘焙网红',
      rating: 4.6,
      distance: '1.8km',
      deliveryTime: '35 分钟',
      monthlySales: 22000,
      minPrice: 15,
      productCount: 52,
      followerCount: '32.5 万',
      joinDate: '2022-12-01',
      tags: ['新中式', '网红品牌', '国潮风'],
      products: [
        { name: '麻薯', price: '18', sales: 9500 },
        { name: '泡芙', price: '22', sales: 7200 },
        { name: '芝士脆', price: '25', sales: 5800 }
      ]
    },
    {
      id: 5,
      name: '原麦山丘',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=YM&backgroundColor=DEB887',
      description: '软欧面包开创者',
      rating: 4.6,
      distance: '3.2km',
      deliveryTime: '45 分钟',
      monthlySales: 11200,
      minPrice: 20,
      productCount: 89,
      followerCount: '18.2 万',
      joinDate: '2023-03-12',
      tags: ['软欧首创', '健康低糖', '网红品牌'],
      products: [
        { name: '紫薯软欧', price: '28', sales: 4500 },
        { name: '金枪鱼面包', price: '26', sales: 3800 },
        { name: '榴莲包', price: '32', sales: 2900 }
      ]
    },
    {
      id: 6,
      name: '21cake',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=21C&backgroundColor=DDA0DD',
      description: '互联网蛋糕品牌，新鲜直达',
      rating: 4.7,
      distance: '5.0km',
      deliveryTime: '60 分钟',
      monthlySales: 9800,
      minPrice: 30,
      productCount: 45,
      followerCount: '15.6 万',
      joinDate: '2022-11-05',
      tags: ['互联网品牌', '当日配送', '简约设计'],
      products: [
        { name: '百利甜', price: '258', sales: 3200 },
        { name: '巧克力慕斯', price: '258', sales: 2800 },
        { name: '芒果拿破仑', price: '268', sales: 2100 }
      ]
    },
    {
      id: 7,
      name: '哈根达斯',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=HD&backgroundColor=DC143C',
      description: '高端冰淇淋品牌',
      rating: 4.8,
      distance: '4.5km',
      deliveryTime: '50 分钟',
      monthlySales: 18500,
      minPrice: 50,
      productCount: 56,
      followerCount: '38.5 万',
      joinDate: '2022-06-20',
      tags: ['高端品牌', '进口原料', '冰淇淋蛋糕'],
      products: [
        { name: '草莓冰淇淋', price: '38', sales: 6500 },
        { name: '比利时巧克力', price: '42', sales: 5800 },
        { name: '抹茶冰淇淋', price: '38', sales: 4200 }
      ]
    },
    {
      id: 8,
      name: '面包新语',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=MBXY&backgroundColor=F5DEB3',
      description: '新加坡品牌，时尚烘焙',
      rating: 4.3,
      distance: '1.5km',
      deliveryTime: '28 分钟',
      monthlySales: 13500,
      minPrice: 12,
      productCount: 178,
      followerCount: '22.1 万',
      joinDate: '2022-09-25',
      tags: ['新加坡品牌', '时尚连锁', '创意烘焙'],
      products: [
        { name: '辣松面包', price: '15', sales: 5600 },
        { name: '芝士面包', price: '18', sales: 4200 },
        { name: '菠萝包', price: '12', sales: 3800 }
      ]
    }
  ],
  cake: [],
  bread: [],
  pastry: [],
  cookie: [],
  chocolate: [],
  icecream: []
}

// 填充其他品类数据
shopsData.cake = shopsData.all.filter(s => ['巴黎贝甜', '好利来', '21cake'].includes(s.name))
shopsData.bread = shopsData.all.filter(s => ['原麦山丘', '面包新语'].includes(s.name))
shopsData.pastry = shopsData.all.filter(s => ['泸溪河', '墨茉点心局'].includes(s.name))
shopsData.icecream = shopsData.all.filter(s => s.name === '哈根达斯')

// 状态
const selectedCategory = ref('all')
const sortType = ref('default')
const searchKeyword = ref('')
const shopDialogVisible = ref(false)
const selectedShop = ref(null)

// 过滤和排序后的商家
const filteredShops = computed(() => {
  let shops = selectedCategory.value === 'all' 
    ? shopsData.all 
    : (shopsData[selectedCategory.value] || [])
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    shops = shops.filter(shop => 
      shop.name.toLowerCase().includes(keyword) ||
      shop.description.toLowerCase().includes(keyword)
    )
  }
  
  // 排序
  if (sortType.value === 'sales') {
    shops = [...shops].sort((a, b) => b.monthlySales - a.monthlySales)
  } else if (sortType.value === 'rating') {
    shops = [...shops].sort((a, b) => b.rating - a.rating)
  } else if (sortType.value === 'distance') {
    shops = [...shops].sort((a, b) => {
      const distA = parseFloat(a.distance)
      const distB = parseFloat(b.distance)
      return distA - distB
    })
  }
  
  return shops
})

// 搜索
const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
}

// 商家点击
const handleShopClick = (shop) => {
  selectedShop.value = shop
  shopDialogVisible.value = true
}

// 点餐
const handleOrder = (shop) => {
  alert(`即将进入 ${shop.name} 的点餐页面`)
}

// 加入购物车
const handleAddToCart = (product) => {
  alert(`已将 ${product.name} 加入购物车`)
}

// 关注商家
const handleFollow = () => {
  alert(`已关注 ${selectedShop.value?.name}`)
  shopDialogVisible.value = false
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  // 初始化加载全部品类
})
</script>

<style scoped>
.meituan-simulator {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-container {
  display: flex;
  min-height: 100vh;
}

/* 左侧边栏 */
.sidebar {
  width: 200px;
  background-color: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: linear-gradient(135deg, #FFD101 0%, #FFA200 100%);
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.sidebar-header .el-icon {
  font-size: 20px;
}

.category-list {
  padding: 10px 0;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background-color: #FFF9E6;
}

.category-item.active {
  background-color: #FFF9E6;
  border-left-color: #FFA200;
  color: #FFA200;
}

.category-icon {
  font-size: 24px;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
}

.category-item.active .category-name {
  color: #FFA200;
  font-weight: 600;
}

/* 内容区 */
.content-area {
  flex: 1;
  overflow-y: auto;
}

/* 搜索头 */
.search-header {
  background: linear-gradient(135deg, #FFD101 0%, #FFA200 100%);
  padding: 15px 20px;
  box-shadow: 0 2px 8px rgba(255, 209, 1, 0.3);
  position: sticky;
  top: 0;
  z-index: 10;
}

.search-bar {
  display: flex;
  gap: 10px;
  max-width: 800px;
  margin: 0 auto;
}

.search-bar :deep(.el-input) {
  flex: 1;
}

.search-bar :deep(.el-input__wrapper) {
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 排序栏 */
.sort-bar {
  background-color: #fff;
  padding: 12px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.sort-options {
  display: flex;
  gap: 20px;
}

.sort-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.sort-item:hover {
  color: #FFA200;
}

.sort-item.active {
  color: #FFA200;
  font-weight: 600;
}

/* 商家列表 */
.shop-list {
  padding: 15px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.shop-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.shop-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.shop-left {
  display: flex;
  gap: 15px;
  flex: 1;
}

.shop-logo {
  flex-shrink: 0;
}

.shop-info {
  flex: 1;
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.shop-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.rating-tag {
  font-weight: bold;
}

.shop-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-meta {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.meta-item .el-icon {
  font-size: 14px;
}

.shop-right {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  margin-left: 20px;
}

.promotion-badge {
  background: linear-gradient(135deg, #FF6B6B, #FF5252);
  color: #fff;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.shop-actions {
  display: flex;
  gap: 10px;
}

/* 商家详情对话框 */
.shop-detail {
  padding: 10px;
}

.detail-banner {
  display: flex;
  gap: 20px;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #FFF9E6, #fff);
  border-radius: 12px;
  margin-bottom: 20px;
}

.banner-info {
  flex: 1;
}

.banner-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.banner-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.banner-rating span {
  color: #FFA200;
  font-weight: bold;
}

.banner-meta {
  font-size: 13px;
  color: #999;
}

.detail-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.promotion-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.promotion-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #666;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  padding: 6px 12px;
}

/* 响应式优化 */
@media screen and (max-width: 768px) {
  .main-container {
    flex-direction: row;
  }
  
  .sidebar {
    width: 80px;
    height: 100vh;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 100;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar-header {
    padding: 10px 8px;
    font-size: 12px;
    flex-direction: column;
    gap: 4px;
  }
  
  .sidebar-header .el-icon {
    font-size: 16px;
  }
  
  .category-list {
    padding: 5px 0;
  }
  
  .category-item {
    flex-direction: column;
    padding: 12px 8px;
    gap: 6px;
    border-left: none;
    border-right: 3px solid transparent;
    justify-content: center;
  }
  
  .category-item:hover {
    background-color: #FFF9E6;
  }
  
  .category-item.active {
    border-left: none;
    border-right-color: #FFA200;
    background-color: #FFF9E6;
  }
  
  .category-icon {
    font-size: 28px;
  }
  
  .category-name {
    font-size: 12px;
    text-align: center;
  }
  
  .content-area {
    margin-left: 80px;
  }
  
  .search-header {
    padding: 10px 15px;
    position: sticky;
  }
  
  .search-bar {
    max-width: none;
  }
  
  .shop-item {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }
  
  .shop-left {
    width: 100%;
  }
  
  .shop-right {
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    margin-left: 0;
  }
  
  .shop-logo .el-avatar {
    width: 60px !important;
    height: 60px !important;
  }
  
  .shop-name {
    font-size: 16px;
  }
  
  .shop-desc {
    font-size: 12px;
  }
  
  .shop-meta {
    gap: 10px;
  }
  
  .meta-item {
    font-size: 11px;
  }
  
  .sort-options {
    gap: 12px;
  }
  
  .sort-item {
    font-size: 13px;
    padding: 4px 8px;
  }
  
  .detail-banner {
    flex-direction: column;
    text-align: center;
  }
}
</style>
