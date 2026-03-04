# AI Code Chat

An intelligent programming Q&A assistant based on large language models.

## Project Overview

AI Code Chat is a frontend-backend separated intelligent programming Q&A system. The backend is built with the Spring Boot framework, and the frontend is constructed using Vue.js + Vite. The system integrates the Qwen large language model to provide intelligent code Q&A, interview question retrieval, and other features.

## Technology Stack

### Backend
- **Framework**: Spring Boot
- **AI Framework**: LangChain4j (integrated with Qwen DashScope)
- **RAG**: Retrieval-Augmented Generation
- **Streaming Response**: Server-Sent Events (SSE)

### Frontend
- **Framework**: Vue 3
- **Build Tool**: Vite
- **Styling**: CSS

## Core Features

1. **Intelligent Q&A**: Code question answering powered by the Qwen large language model.
2. **RAG Retrieval**: Supports content retrieval enhancement via vector databases.
3. **Input Security**: Built-in sensitive word filtering mechanism.
4. **Interview Question Query**: Integrated interview question retrieval tool.
5. **Streaming Response**: Supports real-time streaming output.

## Project Structure

```
ai-code-chat/
├── ai-code-chat-frontend/     # Frontend project
│   ├── src/
│   │   ├── components/        # Vue components
│   │   │   └── ChatRoom.vue   # Chat room component
│   │   ├── App.vue            # Root component
│   │   ├── main.js            # Entry file
│   │   └── style.css          # Global styles
│   ├── index.html             # HTML entry
│   └── vite.config.js         # Vite configuration
│
└── src/main/java/com/harmony/aicodechat/
    ├── AiCodeChatApplication.java     # Main application class
    ├── ai/
    │   ├── AiCodeChatService.java    # AI service interface
    │   ├── AiCodeChatServiceFactory.java
    │   ├── config/                   # Configuration classes
    │   │   ├── CorsConfig.java
    │   │   ├── McpConfig.java
    │   │   ├── QwenChatModelConfig.java
    │   │   └── RagConfig.java
    │   ├── guardrail/                # Security protection
    │   │   └── SafeInputGuardrail.java
    │   ├── listener/                 # Listeners
    │   │   └── ChatModelListenerConfig.java
    │   ├── model/                    # Data models
    │   │   └── QwenChatModelConfig.java
    │   ├── mcp/                      # MCP configuration
    │   │   └── McpConfig.java
    │   └── rag/                      # RAG configuration
    │       └── RagConfig.java
    ├── controller/
    │   └── AiController.java         # REST controller
    └── tools/
        └── InterviewQuestionTool.java # Interview question tool
```

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.8+
- Node.js 16+
- Qwen API Key (DashScope)

### Backend Configuration

1. After cloning the project, edit `src/main/resources/application.yml` to configure the API key:

```yaml
langchain4j:
  community:
    dashscope:
      chat-model:
        api-key: your-api-key
        model-name: qwen-max
```

2. Start the backend service:

```bash
mvn spring-boot:run
```

The backend service runs by default at `http://localhost:8080`.

### Frontend Configuration

1. Navigate to the frontend directory:

```bash
cd ai-code-chat-frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start the development server:

```bash
npm run dev
```

The frontend service runs by default at `http://localhost:5173`.

## API Endpoints

### Chat Endpoint

**GET** `/ai/chat`

| Parameter | Type | Description |
|-----------|------|-------------|
| memoryId  | String | Session ID |
| message   | String | User message |

**Response**: Streaming response via Server-Sent Events (SSE)

## Feature Details

### 1. Input Security Protection
The system includes the `SafeInputGuardrail` component to filter sensitive words in user input, ensuring secure interactions.

### 2. RAG Retrieval Enhancement
Configured via `RagConfig`, vector-based retrieval supports question answering integrated with private knowledge bases.

### 3. Interview Question Retrieval
The `InterviewQuestionTool` retrieves relevant interview questions from mianshiya.com, providing convenience for job seekers.

### 4. Streaming Response
The backend implements streaming output using Flux + ServerSentEvent to enhance user experience.

## License

This project is intended solely for learning and communication purposes.