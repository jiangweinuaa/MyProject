// 在 data() 中添加 hasHistory 字段
data() {
  return {
    question: '',
    loading: false,
    messages: [],
    examples: [
      '今天销售额是多少？',
      '本月销售额是多少？',
      '哪个商品卖得最好？'
    ],
    hasHistory: false  // 是否有历史记录
  }
},

// 添加 mounted 生命周期函数
mounted() {
  this.loadRecentHistory();
},

// 添加加载历史记录的方法
async loadRecentHistory() {
  try {
    // 获取最近的会话列表
    const response = await fetch('/api/conversation/list?userId=default_user&page=0&size=1');
    const data = await response.json();
    
    if (data.success && data.data.length > 0) {
      const latestSession = data.data[0];
      this.sessionId = latestSession.sessionId;
      
      // 加载该会话的对话历史
      await this.loadSessionHistory(latestSession.sessionId);
    }
  } catch (error) {
    console.error('加载历史记录失败:', error);
  }
},

// 加载会话历史的方法
async loadSessionHistory(sessionId) {
  try {
    const response = await fetch(`/api/conversation/history?sessionId=${sessionId}&page=0&size=20`);
    const data = await response.json();
    
    if (data.success && data.data.length > 0) {
      this.hasHistory = true;
      
      // 将历史记录转换为消息格式
      data.data.forEach(dialogue => {
        this.messages.push({
          type: 'user',
          content: dialogue.question,
          time: this.formatTime(dialogue.createdTime)
        });
        
        // 解析结果数据
        let chartData = [];
        try {
          chartData = JSON.parse(dialogue.resultData);
        } catch (e) {
          console.error('解析结果数据失败:', e);
        }
        
        this.messages.push({
          type: 'bot',
          content: `查询完成，共 ${dialogue.rowCount} 条数据`,
          sql: dialogue.sqlGenerated,
          data: chartData,
          time: this.formatTime(dialogue.createdTime)
        });
      });
      
      // 滚动到底部
      this.$nextTick(() => {
        const container = this.$refs.messageContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    }
  } catch (error) {
    console.error('加载会话历史失败:', error);
  }
},

// 格式化时间的方法
formatTime(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now - date;
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
  
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
},
