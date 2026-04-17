<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>404</h1>
      <p>页面不存在</p>
      <el-button type="primary" @click="router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style lang="scss" scoped>
.not-found-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .not-found-content {
    text-align: center;

    h1 {
      font-size: 120px;
      color: $primary-color;
      margin-bottom: 20px;
    }

    p {
      font-size: 24px;
      color: #606266;
      margin-bottom: 30px;
    }
  }
}
</style>
