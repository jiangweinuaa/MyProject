<template>
  <div class="card-query">
    <!-- 上部区块：查询按钮 -->
    <div class="search-section">
      <el-card class="search-card">
        <el-button type="primary" size="large" @click="handleQuery" :loading="loading">
          <el-icon><Search /></el-icon>
          查询所有卡信息
        </el-button>
      </el-card>
    </div>

    <!-- 下部区块：卡信息表格 -->
    <div class="table-section">
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>卡信息列表</span>
            <el-tag v-if="cardList.length > 0" type="success">共 {{ cardList.length }} 条记录</el-tag>
          </div>
        </template>

        <el-table
          :data="cardList"
          style="width: 100%"
          v-loading="loading"
          @row-click="handleRowClick"
          highlight-current-row
          stripe
        >
          <el-table-column prop="cardNo" label="卡号" width="120" />
          <el-table-column prop="cardSnNo" label="卡序列号" width="200" />
          <el-table-column prop="memberName" label="会员姓名" width="150" />
          <el-table-column prop="memberId" label="会员 ID" width="150" />
          <el-table-column prop="amount" label="余额" width="120" align="right">
            <template #default="scope">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="faceAmount" label="面值" width="120" align="right" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cardTypeId" label="卡类型" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="150" />
          <el-table-column prop="remark" label="备注" />
        </el-table>

        <el-empty v-if="!loading && cardList.length === 0" description="点击查询按钮加载数据" />
      </el-card>
    </div>

    <!-- 弹出窗口：卡详情 -->
    <el-dialog
      v-model="dialogVisible"
      title="卡详细信息"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions
        v-if="selectedCard"
        title="基本信息"
        :column="3"
        border
      >
        <el-descriptions-item label="卡 ID">{{ selectedCard.cardId }}</el-descriptions-item>
        <el-descriptions-item label="卡号">{{ selectedCard.cardNo }}</el-descriptions-item>
        <el-descriptions-item label="卡序列号">{{ selectedCard.cardSnNo }}</el-descriptions-item>
        <el-descriptions-item label="会员 ID">{{ selectedCard.memberId }}</el-descriptions-item>
        <el-descriptions-item label="会员姓名">{{ selectedCard.memberName }}</el-descriptions-item>
        <el-descriptions-item label="卡类型">{{ selectedCard.cardTypeId }}</el-descriptions-item>
        <el-descriptions-item label="余额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ selectedCard.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="面值">¥{{ selectedCard.faceAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedCard.status === 1 ? 'success' : 'danger'">
            {{ selectedCard.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions
        v-if="selectedCard"
        title="日期信息"
        :column="3"
        border
        style="margin-top: 20px"
      >
        <el-descriptions-item label="生成日期">{{ selectedCard.generateDate }}</el-descriptions-item>
        <el-descriptions-item label="发卡日期">{{ selectedCard.issueDate }}</el-descriptions-item>
        <el-descriptions-item label="激活日期">{{ selectedCard.activeDate }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedCard.createTime }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ selectedCard.createOpId }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ selectedCard.remark }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { queryAllCards, queryCard } from '../api/card'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const cardList = ref([])
const dialogVisible = ref(false)
const selectedCard = ref(null)

// 查询所有卡片
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await queryAllCards()
    if (res.data && Array.isArray(res.data)) {
      cardList.value = res.data
      ElMessage.success(`查询成功，共 ${res.data.length} 条记录`)
    } else {
      ElMessage.warning('未查询到数据')
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 点击行，查询详情
const handleRowClick = async (row) => {
  loading.value = true
  try {
    const res = await queryCard(row.cardNo)
    if (res.data && res.data.code === 200) {
      selectedCard.value = res.data
      dialogVisible.value = true
    } else {
      ElMessage.warning('未查询到该卡详细信息')
    }
  } catch (error) {
    console.error('查询详情失败:', error)
    ElMessage.error('查询详情失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.card-query {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-section {
  flex-shrink: 0;
}

.search-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.search-card :deep(.el-card__body) {
  display: flex;
  justify-content: center;
  padding: 30px;
}

.table-section {
  flex: 1;
  overflow: hidden;
}

.table-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.el-table__row--highlight) {
  background-color: #ecf5ff !important;
}
</style>
