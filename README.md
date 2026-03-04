

# AI Code Chat

基于大语言模型的智能编程问答助手

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.4-4FC08D.svg)](https://vuejs.org/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.1.0-blue.svg)](https://github.com/langchain4j/langchain4j)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)


## 项目简介

AI Code Chat 是一个前后端分离的智能编程问答系统，后端采用 Spring Boot 框架，前端使用 Vue.js + Vite 构建。系统集成了通义千问（Qwen）大模型，提供智能代码问答、面试题检索等功能。

## 技术栈

### 后端
- **框架**: Spring Boot
- **AI 框架**: LangChain4j (集成通义千问 DashScope)
- **RAG**: 检索增强生成 (Retrieval-Augmented Generation)
- **流式响应**: Server-Sent Events (SSE)

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **样式**: CSS

## 核心功能

1. **智能问答**: 基于通义千问大模型进行代码问答
2. **RAG 检索**: 支持基于向量数据库的内容检索增强
3. **输入安全**: 内置敏感词过滤机制
4. **面试题查询**: 集成面试题检索工具
5. **流式响应**: 支持实时流式输出

## 项目结构

```
ai-code-chat/
├── ai-code-chat-frontend/     # 前端项目
│   ├── src/
│   │   ├── components/        # Vue 组件
│   │   │   └── ChatRoom.vue   # 聊天室组件
│   │   ├── App.vue            # 根组件
│   │   ├── main.js            # 入口文件
│   │   └── style.css          # 全局样式
│   ├── index.html             # HTML 入口
│   └── vite.config.js         # Vite 配置
│
└── src/main/java/com/harmony/aicodechat/
    ├── AiCodeChatApplication.java     # 启动类
    ├── ai/
    │   ├── AiCodeChatService.java    # AI 服务接口
    │   ├── AiCodeChatServiceFactory.java
    │   ├── config/                   # 配置类
    │   │   ├── CorsConfig.java
    │   │   ├── McpConfig.java
    │   │   ├── QwenChatModelConfig.java
    │   │   └── RagConfig.java
    │   ├── guardrail/                # 安全防护
    │   │   └── SafeInputGuardrail.java
    │   ├── listener/                 # 监听器
    │   │   └── ChatModelListenerConfig.java
    │   ├── model/                    # 数据模型
    │   │   └── QwenChatModelConfig.java
    │   ├── mcp/                      # MCP 配置
    │   │   └── McpConfig.java
    │   └── rag/                      # RAG 配置
    │       └── RagConfig.java
    ├── controller/
    │   └── AiController.java         # REST 控制器
    └── tools/
        └── InterviewQuestionTool.java # 面试题工具
```

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Node.js 16+
- 通义千问 API Key (DashScope)

### 后端配置

1. 克隆项目后，编辑 `src/main/resources/application.yml` 配置 API Key：

```yaml
langchain4j:
  community:
    dashscope:
      chat-model:
        api-key: your-api-key
        model-name: qwen-max
```

2. 启动后端服务：

```bash
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 前端配置

1. 进入前端目录：

```bash
cd ai-code-chat-frontend
```

2. 安装依赖：

```bash
npm install
```

3. 启动开发服务器：

```bash
npm run dev
```

前端服务默认运行在 `http://localhost:5173`

## API 接口

### 聊天接口

**GET** `/ai/chat`

| 参数 | 类型 | 说明 |
|------|------|------|
| memoryId | String | 会话 ID |
| message | String | 用户消息 |

**响应**: Server-Sent Events 流式返回

## 功能特性详解

### 1. 输入安全防护
系统内置 `SafeInputGuardrail` 组件，对用户输入进行敏感词过滤，确保交互安全。

### 2. RAG 检索增强
通过 `RagConfig` 配置向量检索，支持结合私有知识库进行问答。

### 3. 面试题检索
`InterviewQuestionTool` 工具可从 mianshiya.com 检索相关面试题，为求职者提供便利。

### 4. 流式响应
后端采用 Flux + ServerSentEvent 实现流式输出，提升用户体验。

## 许可证

本项目仅供学习交流使用。