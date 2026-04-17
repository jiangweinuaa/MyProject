<template>
  <el-dialog v-model="visible" :title="title" :width="width" :close-on-click-modal="false" @close="handleClose">
    <slot>默认内容</slot>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="confirmLoading" @click="handleConfirm">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: boolean
  title?: string
  width?: string
  confirmLoading?: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm'): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  title: '提示',
  width: '500px',
  confirmLoading: false
})

const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const handleClose = () => {
  emit('cancel')
}

const handleCancel = () => {
  emit('cancel')
  visible.value = false
}

const handleConfirm = () => {
  emit('confirm')
  if (!props.confirmLoading) {
    visible.value = false
  }
}
</script>

<style lang="scss" scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
