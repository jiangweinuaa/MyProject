<template>
  <el-card class="search-card" :body-style="{ padding: '16px' }">
    <el-form :inline="true" :model="formData" label-width="80px" size="default">
      <slot name="form-items"></slot>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

interface Props {
  modelValue: Record<string, any>
}

interface Emits {
  (e: 'update:modelValue', value: Record<string, any>): void
  (e: 'search', value: Record<string, any>): void
  (e: 'reset', value: Record<string, any>): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formData = reactive({ ...props.modelValue })

const handleSearch = () => {
  emit('update:modelValue', { ...formData })
  emit('search', { ...formData })
}

const handleReset = () => {
  const resetData: Record<string, any> = {}
  for (const key in formData) {
    resetData[key] = ''
  }
  Object.assign(formData, resetData)
  emit('update:modelValue', resetData)
  emit('reset', resetData)
}
</script>

<style lang="scss" scoped>
.search-card {
  margin-bottom: 16px;

  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 16px;
  }
}
</style>
