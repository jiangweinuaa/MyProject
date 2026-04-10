<template>
  <div class="selection-simulator">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">🏆 甄选模拟</span>
    </div>

    <!-- 品类选择 -->
    <el-card class="category-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Filter /></el-icon>
          <span>选择品类</span>
        </div>
      </template>
      <el-radio-group v-model="selectedCategory" @change="handleCategoryChange" size="large">
        <el-radio-button 
          v-for="cat in categories" 
          :key="cat.id" 
          :label="cat.id"
        >
          {{ cat.icon }} {{ cat.name }}
        </el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 商家列表 -->
    <el-row :gutter="20" class="shop-row">
      <el-col 
        v-for="shop in filteredShops" 
        :key="shop.id" 
        :xs="24" 
        :sm="12" 
        :md="8" 
        :lg="6"
        class="shop-col"
      >
        <el-card 
          shadow="hover" 
          class="shop-card"
          :body-style="{ padding: '0' }"
          @click="handleShopClick(shop)"
        >
          <div class="shop-logo">
            <el-avatar :size="80" :src="shop.logo" shape="square">
              <img :src="shop.logo" :alt="shop.name" />
            </el-avatar>
            <el-tag 
              :type="shop.rating >= 4.5 ? 'success' : (shop.rating >= 4 ? 'primary' : 'warning')" 
              size="small"
              class="rating-tag"
            >
              {{ shop.rating }}分
            </el-tag>
          </div>
          
          <div class="shop-info">
            <div class="shop-name">{{ shop.name }}</div>
            <div class="shop-desc">{{ shop.description }}</div>
            
            <div class="shop-stats">
              <div class="stat-item">
                <el-icon><Shop /></el-icon>
                <span>{{ shop.productCount }} 款商品</span>
              </div>
              <div class="stat-item">
                <el-icon><User /></el-icon>
                <span>{{ shop.followerCount }} 粉丝</span>
              </div>
            </div>
            
            <div class="shop-tags">
              <el-tag 
                v-for="tag in shop.tags" 
                :key="tag" 
                size="small" 
                effect="plain"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
          
          <div class="shop-footer">
            <el-button type="primary" plain size="small" round>
              查看详情
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商家详情对话框 -->
    <el-dialog 
      v-model="shopDialogVisible" 
      :title="selectedShop?.name" 
      width="600px"
      class="shop-dialog"
    >
      <div class="shop-detail" v-if="selectedShop">
        <div class="detail-header">
          <el-avatar :size="100" :src="selectedShop.logo" shape="square" />
          <div class="detail-info">
            <div class="detail-name">{{ selectedShop.name }}</div>
            <div class="detail-rating">
              <el-rate v-model="selectedShop.rating" disabled />
              <span>{{ selectedShop.rating }}分</span>
            </div>
            <div class="detail-desc">{{ selectedShop.description }}</div>
          </div>
        </div>
        
        <el-divider />
        
        <div class="detail-content">
          <el-descriptions title="商家信息" :column="1" border>
            <el-descriptions-item label="入驻时间">{{ selectedShop.joinDate }}</el-descriptions-item>
            <el-descriptions-item label="商品数量">{{ selectedShop.productCount }} 款</el-descriptions-item>
            <el-descriptions-item label="粉丝数量">{{ selectedShop.followerCount }}</el-descriptions-item>
            <el-descriptions-item label="月销量">{{ selectedShop.monthlySales }} 单</el-descriptions-item>
          </el-descriptions>
          
          <el-divider />
          
          <div class="tags-section">
            <div class="section-title">特色标签</div>
            <div class="tags-container">
              <el-tag 
                v-for="tag in selectedShop.tags" 
                :key="tag" 
                size="default"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
          
          <div class="products-section">
            <div class="section-title">热门商品</div>
            <el-table :data="selectedShop.products" style="width: 100%" size="small">
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="price" label="价格" width="100" align="right">
                <template #default="{ row }">
                  <span style="color: #F56C6C; font-weight: bold">¥{{ row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="sales" label="销量" width="80" align="center" />
            </el-table>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="shopDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleFollow">关注商家</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Filter, Shop, User, Star } from '@element-plus/icons-vue'

const router = useRouter()

// 品类列表
const categories = [
  { id: 'cake', name: '蛋糕', icon: '🎂' },
  { id: 'bread', name: '面包', icon: '🍞' },
  { id: 'pastry', name: '糕点', icon: '🥐' },
  { id: 'cookie', name: '饼干', icon: '🍪' },
  { id: 'chocolate', name: '巧克力', icon: '🍫' },
  { id: 'icecream', name: '冰淇淋', icon: '🍦' }
]

// 商家数据
const shopsData = {
  cake: [
    {
      id: 1,
      name: '巴黎贝甜',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=PB&backgroundColor=FFB6C1',
      description: '法式烘焙经典，传承百年工艺',
      rating: 4.8,
      productCount: 156,
      followerCount: '12.5 万',
      monthlySales: 8520,
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
      productCount: 203,
      followerCount: '28.3 万',
      monthlySales: 15200,
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
      name: '元祖食品',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=YG&backgroundColor=98FB98',
      description: '精致糕点，礼赠佳品',
      rating: 4.5,
      productCount: 128,
      followerCount: '9.8 万',
      monthlySales: 6300,
      joinDate: '2023-01-10',
      tags: ['礼赠首选', '传统工艺', '节庆限定'],
      products: [
        { name: '雪月饼', price: '388', sales: 2100 },
        { name: '慕斯蛋糕', price: '258', sales: 1580 },
        { name: '年轮蛋糕', price: '198', sales: 1200 }
      ]
    },
    {
      id: 4,
      name: '21cake',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=21C&backgroundColor=DDA0DD',
      description: '互联网蛋糕品牌，新鲜直达',
      rating: 4.7,
      productCount: 45,
      followerCount: '15.6 万',
      monthlySales: 9800,
      joinDate: '2022-11-05',
      tags: ['互联网品牌', '当日配送', '简约设计'],
      products: [
        { name: '百利甜', price: '258', sales: 3200 },
        { name: '巧克力慕斯', price: '258', sales: 2800 },
        { name: '芒果拿破仑', price: '268', sales: 2100 }
      ]
    }
  ],
  bread: [
    {
      id: 5,
      name: '原麦山丘',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=YM&backgroundColor=DEB887',
      description: '软欧面包开创者',
      rating: 4.6,
      productCount: 89,
      followerCount: '18.2 万',
      monthlySales: 11200,
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
      name: '多乐之日',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=DLZR&backgroundColor=F0E68C',
      description: '韩式烘焙，健康美味',
      rating: 4.4,
      productCount: 156,
      followerCount: '10.5 万',
      monthlySales: 7800,
      joinDate: '2023-06-18',
      tags: ['韩式风味', '健康烘焙', '早餐首选'],
      products: [
        { name: '红豆面包', price: '18', sales: 3200 },
        { name: '奶油号角', price: '16', sales: 2800 },
        { name: '全麦吐司', price: '22', sales: 2400 }
      ]
    },
    {
      id: 7,
      name: '面包新语',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=MBXY&backgroundColor=F5DEB3',
      description: '新加坡品牌，时尚烘焙',
      rating: 4.3,
      productCount: 178,
      followerCount: '22.1 万',
      monthlySales: 13500,
      joinDate: '2022-09-25',
      tags: ['新加坡品牌', '时尚连锁', '创意烘焙'],
      products: [
        { name: '辣松面包', price: '15', sales: 5600 },
        { name: '芝士面包', price: '18', sales: 4200 },
        { name: '菠萝包', price: '12', sales: 3800 }
      ]
    }
  ],
  pastry: [
    {
      id: 8,
      name: '泸溪河',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=LXH&backgroundColor=FFE4B5',
      description: '中式糕点老字号',
      rating: 4.7,
      productCount: 95,
      followerCount: '25.8 万',
      monthlySales: 18500,
      joinDate: '2022-07-08',
      tags: ['中式传统', '老字号', '现烤现卖'],
      products: [
        { name: '桃酥', price: '28', sales: 8500 },
        { name: '绿豆饼', price: '25', sales: 6200 },
        { name: '蛋黄酥', price: '35', sales: 5100 }
      ]
    },
    {
      id: 9,
      name: '詹记宫廷桃酥',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=ZJ&backgroundColor=FFDAB9',
      description: '宫廷配方，传统美味',
      rating: 4.5,
      productCount: 68,
      followerCount: '16.3 万',
      monthlySales: 12800,
      joinDate: '2023-02-14',
      tags: ['宫廷配方', '非遗技艺', '伴手礼'],
      products: [
        { name: '宫廷桃酥', price: '32', sales: 6800 },
        { name: '无水蛋糕', price: '28', sales: 4200 },
        { name: '蜜三刀', price: '26', sales: 3500 }
      ]
    },
    {
      id: 10,
      name: '墨茉点心局',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=MM&backgroundColor=FFC0CB',
      description: '新中式烘焙网红',
      rating: 4.6,
      productCount: 52,
      followerCount: '32.5 万',
      monthlySales: 22000,
      joinDate: '2022-12-01',
      tags: ['新中式', '网红品牌', '国潮风'],
      products: [
        { name: '麻薯', price: '18', sales: 9500 },
        { name: '泡芙', price: '22', sales: 7200 },
        { name: '芝士脆', price: '25', sales: 5800 }
      ]
    }
  ],
  cookie: [
    {
      id: 11,
      name: '奥利奥',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=OREO&backgroundColor=4169E1',
      description: '全球经典饼干品牌',
      rating: 4.8,
      productCount: 45,
      followerCount: '58.2 万',
      monthlySales: 35000,
      joinDate: '2022-05-10',
      tags: ['国际品牌', '经典口味', '儿童喜爱'],
      products: [
        { name: '原味夹心', price: '8', sales: 15000 },
        { name: '巧克力味', price: '8', sales: 12000 },
        { name: '草莓味', price: '8', sales: 8500 }
      ]
    }
  ],
  chocolate: [
    {
      id: 12,
      name: '费列罗',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=FERRERO&backgroundColor=FFD700',
      description: '意大利巧克力品牌',
      rating: 4.9,
      productCount: 38,
      followerCount: '42.6 万',
      monthlySales: 28000,
      joinDate: '2022-04-15',
      tags: ['意大利进口', '高端礼品', '榛果威化'],
      products: [
        { name: '金莎榛果', price: '68', sales: 12000 },
        { name: '巧克力礼盒', price: '198', sales: 8500 },
        { name: ' Raffaello', price: '58', sales: 6200 }
      ]
    }
  ],
  icecream: [
    {
      id: 13,
      name: '哈根达斯',
      logo: 'https://api.dicebear.com/7.x/initials/svg?seed=HD&backgroundColor=DC143C',
      description: '高端冰淇淋品牌',
      rating: 4.8,
      productCount: 56,
      followerCount: '38.5 万',
      monthlySales: 18500,
      joinDate: '2022-06-20',
      tags: ['高端品牌', '进口原料', '冰淇淋蛋糕'],
      products: [
        { name: '草莓冰淇淋', price: '38', sales: 6500 },
        { name: '比利时巧克力', price: '42', sales: 5800 },
        { name: '抹茶冰淇淋', price: '38', sales: 4200 }
      ]
    }
  ]
}

// 选中的品类
const selectedCategory = ref('cake')

// 商家列表
const currentShops = ref([])

// 对话框
const shopDialogVisible = ref(false)
const selectedShop = ref(null)

// 过滤后的商家
const filteredShops = computed(() => {
  return currentShops.value
})

// 处理品类切换
const handleCategoryChange = (categoryId) => {
  currentShops.value = shopsData[categoryId] || []
}

// 处理商家点击
const handleShopClick = (shop) => {
  selectedShop.value = shop
  shopDialogVisible.value = true
}

// 关注商家
const handleFollow = () => {
  // 模拟关注操作
  alert(`已关注 ${selectedShop.value?.name}`)
  shopDialogVisible.value = false
}

// 返回
const goBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  // 默认加载蛋糕品类
  handleCategoryChange('cake')
})
</script>

<style scoped>
.selection-simulator {
  padding: 15px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.category-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  width: 100%;
}

:deep(.el-radio-button) {
  margin-bottom: 10px;
}

:deep(.el-radio-button__inner) {
  padding: 12px 20px;
  font-size: 15px;
}

.shop-row {
  margin-top: 10px;
}

.shop-col {
  margin-bottom: 20px;
}

.shop-card {
  transition: all 0.3s;
  cursor: pointer;
  height: 100%;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.shop-logo {
  position: relative;
  height: 140px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.shop-logo .el-avatar {
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  background-color: #fff;
}

.rating-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  font-weight: bold;
}

.shop-info {
  padding: 15px;
}

.shop-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.shop-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  line-height: 1.5;
}

.shop-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #606266;
}

.stat-item .el-icon {
  color: #909399;
}

.shop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-item {
  margin-right: 5px;
  margin-bottom: 5px;
}

.shop-footer {
  padding: 12px 15px;
  border-top: 1px solid #EBEEF5;
  text-align: center;
}

/* 商家详情对话框 */
.shop-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 15px;
}

.detail-info {
  flex: 1;
}

.detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.detail-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.detail-rating span {
  color: #F7BA2A;
  font-weight: bold;
}

.detail-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
}

.tags-section,
.products-section {
  margin-top: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 响应式优化 */
@media screen and (max-width: 768px) {
  .selection-simulator {
    padding: 10px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  :deep(.el-radio-button__inner) {
    padding: 10px 15px;
    font-size: 14px;
  }
  
  .shop-name {
    font-size: 16px;
  }
}
</style>
