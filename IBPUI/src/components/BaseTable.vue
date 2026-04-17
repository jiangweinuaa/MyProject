<template>
  <el-card class="table-card" :body-style="{ padding: '0' }">
    <el-table
      v-loading="loading"
      :data="data"
      :border="border"
      :stripe="stripe"
      :height="height"
      :max-height="maxHeight"
      style="width: 100%"
    >
      <el-table-column v-if="selection" type="selection" width="55" align="center" />
      <el-table-column v-if="index" type="index" label="序号" width="60" align="center" />
      
      <slot name="columns"></slot>
      
      <el-table-column v-if="operations" label="操作" width="200" align="center" fixed="right">
        <template #default="{ row, $index }">
          <slot name="operations" :row="row" :index="$index"></slot>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="pagination" class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  data: any[]
  loading?: boolean
  border?: boolean
  stripe?: boolean
  height?: number | string
  maxHeight?: number | string
  selection?: boolean
  index?: boolean
  operations?: boolean
  pagination?: boolean
  total?: number
  page?: number
  pageSize?: number
  pageSizes?: number[]
}

interface Emits {
  (e: 'update:page', value: number): void
  (e: 'update:pageSize', value: number): void
  (e: 'pageChange', page: number, pageSize: number): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  border: true,
  stripe: false,
  pagination: true,
  page: 1,
  pageSize: 10,
  pageSizes: () => [10, 20, 50, 100]
})

const emit = defineEmits<Emits>()

const currentPage = ref(props.page)
const currentPageSize = ref(props.pageSize)

watch(
  () => props.page,
  (val) => {
    currentPage.value = val
  }
)

watch(
  () => props.pageSize,
  (val) => {
    currentPageSize.value = val
  }
)

const handleSizeChange = (size: number) => {
  currentPageSize.value = size
  emit('update:pageSize', size)
  emit('pageChange', currentPage.value, size)
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  emit('update:page', page)
  emit('pageChange', page, currentPageSize.value)
}
</script>

<style lang="scss" scoped>
.table-card {
  .pagination-container {
    padding: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
