<template>
  <div class="retrain-container">
    <iframe 
      src="http://47.99.153.144:8081/retrain.html" 
      frameborder="0" 
      class="retrain-iframe"
    ></iframe>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log('商品重新训练页面已加载')
})
</script>

<style scoped>
.retrain-container {
  width: 100%;
  height: calc(100vh - 120px);
  overflow: hidden;
}

.retrain-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
