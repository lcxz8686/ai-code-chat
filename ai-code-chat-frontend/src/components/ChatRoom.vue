<template>
  <div class="chat-room">
    <div class="chat-header">
      <div class="chat-header-main">
        <h1>AI 编程小助手</h1>
        <p class="chat-subtitle">专注编程学习与面试辅导的智能助手</p>
      </div>
      <div class="chat-header-meta">
        <span class="chat-info-label">会话 ID</span>
        <span class="chat-info-value">{{ memoryId }}</span>
      </div>
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
          <div class="message-text" v-html="renderMarkdown(message.content)"></div>
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
import { marked } from 'marked'

const renderer = new marked.Renderer()
renderer.link = (href, title, text) => {
  const safeHref = href || '#'
  const titleAttr = title ? ` title="${title}"` : ''
  return `<a href="${safeHref}" target="_blank" rel="noopener noreferrer"${titleAttr}>${text}</a>`
}
marked.use({ renderer })

const renderMarkdown = (content) => marked.parse(content || '')

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
  
  const url = `http://localhost:8081/api/ai/chat?memoryId=${(memoryId.value)}&message=${encodeURIComponent(message)}`
  
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
  background: radial-gradient(circle at top left, #e3f2fd 0, #f8f9fa 40%, #f3f4ff 100%);
}

.chat-header {
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  padding: 18px 32px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.chat-header-main {
  max-width: 70%;
}

.chat-header h1 {
  font-size: 24px;
  margin-bottom: 6px;
  font-weight: 600;
}

.chat-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.chat-header-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.chat-info-label {
  font-size: 12px;
  opacity: 0.85;
}

.chat-info-value {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 12px;
  letter-spacing: 0.02em;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px 24px 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
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
  align-self: stretch;
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
  align-self: stretch;
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
  max-width: 72%;
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

.message-text h1,
.message-text h2,
.message-text h3,
.message-text h4,
.message-text h5,
.message-text h6 {
  margin: 16px 0 8px 0;
  font-weight: 600;
  line-height: 1.3;
}

.message-text h1 {
  font-size: 24px;
}

.message-text h2 {
  font-size: 20px;
}

.message-text h3 {
  font-size: 18px;
}

.message-text p {
  margin: 8px 0;
}

.message-text ul,
.message-text ol {
  margin: 8px 0;
  padding-left: 24px;
}

.message-text li {
  margin: 4px 0;
}

.message-text code {
  background-color: #f1f3f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
}

.message-text pre {
  background-color: #f1f3f4;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 12px 0;
}

.message-text pre code {
  background-color: transparent;
  padding: 0;
}

.message-text blockquote {
  border-left: 4px solid #4361ee;
  padding-left: 16px;
  margin: 12px 0;
  color: #666;
  font-style: italic;
}

.message-text a {
  color: #4361ee;
  text-decoration: none;
}

.message-text a:hover {
  text-decoration: underline;
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
  padding: 30px 10px 10px;
  border-top: 1px solid #e9ecef;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
}

.input-container {
  width: 100%;
  max-width: 760px;
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

/* 小屏幕适配 */
@media (max-width: 768px) {
  .chat-header {
    padding: 14px 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .chat-header-main {
    max-width: 100%;
  }

  .chat-header h1 {
    font-size: 20px;
  }

  .chat-subtitle {
    font-size: 13px;
  }

  .chat-header-meta {
    align-items: flex-start;
  }

  .chat-messages {
    padding: 16px 12px 28px;
  }

  .welcome-message {
    flex-direction: column;
    margin-top: 24px;
  }

  .message {
    max-width: 100%;
  }

  .message-content {
    max-width: 100%;
  }

  .chat-input {
    padding: 10px 12px 16px;
  }

  .input-container {
    border-radius: 10px;
  }

  .input-container textarea {
    min-height: 80px;
  }
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
