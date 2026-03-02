<template>
  <div class="chat-room">
    <div class="chat-header">
      <h1>AI 编程小助手</h1>
      <div class="chat-info">会话ID: {{ memoryId }}</div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <!-- 欢迎信息 -->
      <div v-if="messages.length === 0" class="welcome-message">
        <div class="welcome-avatar">
          <div class="avatar-icon">🤖</div>
        </div>
        <div class="welcome-content">
          <h3>你好！我是AI编程小助手</h3>
          <p>我是AI编程助手，你遇到的任何编程学习和求职面试相关的问题我都可以解答！</p>
          <p>请描述一下你的问题？</p>
          
          <!-- 示例问题 -->
          <div class="example-questions">
            <div 
              v-for="(example, index) in exampleQuestions" 
              :key="index"
              class="example-question"
              @click="inputMessage = example; sendMessage()"
            >
              {{ example }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 聊天记录 -->
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message', message.role]"
      >
        <div v-if="message.role === 'ai'" class="message-avatar">
          <div class="avatar-icon">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-role">{{ message.role === 'user' ? '用户' : 'AI助手' }}</div>
          <div class="message-text">{{ message.content }}</div>
        </div>
        <div v-if="message.role === 'user'" class="message-avatar user-avatar">
          <div class="avatar-icon">👤</div>
        </div>
      </div>
      
      <!-- 流式输出 -->
      <div v-if="isStreaming" class="message ai streaming">
        <div class="message-avatar">
          <div class="avatar-icon">🤖</div>
        </div>
        <div class="message-content">
          <div class="message-role">AI助手</div>
          <div class="message-text">{{ streamingContent }}<span class="cursor">|</span></div>
        </div>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="chat-input">
      <div class="input-container">
        <div class="input-toolbar">
          <button class="tool-button">
            🖼️
          </button>
        </div>
        <textarea 
          v-model="inputMessage" 
          placeholder="输入您的问题，Ctrl+回车发送"
          @keydown.enter.prevent="sendMessage"
          :disabled="isStreaming"
          rows="3"
        ></textarea>
        <div class="input-footer">
          <span class="input-hint">Ctrl+回车发送</span>
          <button 
            @click="sendMessage" 
            :disabled="!inputMessage.trim() || isStreaming"
            class="send-button"
          >
            {{ isStreaming ? '生成中...' : '发送' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'

const messages = ref([])
const inputMessage = ref('')
const memoryId = ref('')
const isStreaming = ref(false)
const streamingContent = ref('')
const messagesContainer = ref(null)
let eventSource = null

// 示例问题
const exampleQuestions = [
  '如何学习前端开发？',
  'Python 面试常见问题有哪些？',
  '如何优化 React 应用性能？',
  '解释一下闭包的概念',
  '数据库索引的工作原理是什么？',
  '如何准备技术面试？'
]

const generateMemoryId = () => {
  return Date.now().toString()
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const sendMessage = () => {
  const message = inputMessage.value.trim()
  if (!message || isStreaming.value) return

  messages.value.push({
    role: 'user',
    content: message
  })
  
  inputMessage.value = ''
  scrollToBottom()
  
  startStream(message)
}

const startStream = (message) => {
  isStreaming.value = true
  streamingContent.value = ''
  
  const url = `http://localhost:8081/api/ai/chat?memoryId=${memoryId.value}&message=${encodeURIComponent(message)}`
  
  eventSource = new EventSource(url)
  
  eventSource.onmessage = (event) => {
    const chunk = event.data
    if (chunk) {
      streamingContent.value += chunk
      scrollToBottom()
    }
  }
  
  eventSource.onerror = (error) => {
    console.error('SSE error:', error)
    eventSource?.close()
    
    if (streamingContent.value) {
      messages.value.push({
        role: 'ai',
        content: streamingContent.value
      })
    }
    
    isStreaming.value = false
    streamingContent.value = ''
    scrollToBottom()
  }
  
  eventSource.addEventListener('complete', () => {
    eventSource?.close()
    
    if (streamingContent.value) {
      messages.value.push({
        role: 'ai',
        content: streamingContent.value
      })
    }
    
    isStreaming.value = false
    streamingContent.value = ''
    scrollToBottom()
  })
}

onMounted(() => {
  memoryId.value = generateMemoryId()
})

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f9fa;
}

.chat-header {
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chat-header h1 {
  font-size: 24px;
  margin-bottom: 8px;
  font-weight: 600;
}

.chat-info {
  font-size: 14px;
  opacity: 0.9;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 欢迎信息 */
.welcome-message {
  display: flex;
  gap: 16px;
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  max-width: 800px;
  align-self: center;
  margin-top: 40px;
}

.welcome-avatar {
  flex-shrink: 0;
}

.avatar-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #e3f2fd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar .avatar-icon {
  background: #f3e5f5;
}

.welcome-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.welcome-content p {
  font-size: 15px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.6;
}

/* 示例问题 */
.example-questions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.example-question {
  padding: 12px 16px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  color: #495057;
  cursor: pointer;
  transition: all 0.3s ease;
}

.example-question:hover {
  background: #e3f2fd;
  border-color: #90caf9;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 消息样式 */
.message {
  display: flex;
  align-items: flex-start;
  animation: slideIn 0.3s ease-out;
  gap: 12px;
  max-width: 800px;
  align-self: flex-start;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin-top: 4px;
}

.message-content {
  max-width: 70%;
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message.user .message-content {
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.ai .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
}

.message.streaming .message-content {
  border: 2px solid #4361ee;
}

.message-role {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
  opacity: 0.9;
}

.message-text {
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.cursor {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 输入区域 */
.chat-input {
  background: white;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
}

.input-toolbar {
  padding: 8px 16px;
  border-bottom: 1px solid #e9ecef;
  background: white;
}

.tool-button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.tool-button:hover {
  background: #e9ecef;
}

.input-container textarea {
  width: 100%;
  padding: 16px;
  border: none;
  background: #f8f9fa;
  font-size: 15px;
  font-family: inherit;
  resize: none;
  outline: none;
  min-height: 100px;
}

.input-container textarea:focus {
  background: white;
}

.input-container textarea:disabled {
  background-color: #f1f3f4;
  cursor: not-allowed;
}

.input-footer {
  padding: 12px 16px;
  border-top: 1px solid #e9ecef;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-hint {
  font-size: 12px;
  color: #6c757d;
}

.send-button {
  padding: 10px 20px;
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.4);
}

.send-button:active:not(:disabled) {
  transform: translateY(0);
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
