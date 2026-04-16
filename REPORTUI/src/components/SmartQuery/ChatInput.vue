<template>
  <div class="chat-input">
    <el-input
      v-model="inputValue"
      placeholder="问我：今天销售额是多少？"
      @keyup.enter="handleSend"
      :disabled="loading"
      size="large"
      class="full-width-input"
    >
      <template #append>
        <el-button type="primary" @click="handleSend" :loading="loading" size="large">
          发送
        </el-button>
      </template>
    </el-input>
  </div>
</template>

<script>
export default {
  name: 'ChatInput',
  props: {
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      inputValue: ''
    }
  },
  methods: {
    handleSend() {
      if (!this.inputValue.trim() || this.loading) return
      
      // 发送消息到父组件
      this.$emit('send', this.inputValue.trim())
      
      // 清空输入框
      this.inputValue = ''
    }
  }
}
</script>

<style scoped>
.chat-input {
  padding: 15px 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.chat-input .full-width-input {
  width: 100%;
}

.chat-input .full-width-input .el-input__wrapper {
  width: 100%;
  box-sizing: border-box;
}
</style>
