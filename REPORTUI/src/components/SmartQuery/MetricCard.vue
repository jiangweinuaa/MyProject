<template>
  <div class="metric-card">
    <div class="metric-value">{{ value }}</div>
    <div class="metric-label">{{ label }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: Array
})

const value = computed(() => {
  if (!props.data || props.data.length === 0) return '-'
  const firstRow = props.data[0]
  const values = Object.values(firstRow)
  return values[0] !== null && values[0] !== undefined ? values[0] : '-'
})

const label = computed(() => {
  if (!props.data || props.data.length === 0) return ''
  return Object.keys(props.data[0])[0]
})
</script>

<style scoped>
.metric-card {
  text-align: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  margin-bottom: 15px;
}

.metric-value {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 10px;
}

.metric-label {
  font-size: 16px;
  opacity: 0.9;
  text-transform: uppercase;
  letter-spacing: 1px;
}
</style>
