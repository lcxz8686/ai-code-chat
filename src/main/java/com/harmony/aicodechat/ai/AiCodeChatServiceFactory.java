package com.harmony.aicodechat.ai;

import com.harmony.aicodechat.ai.model.QwenChatModelConfig;
import com.harmony.aicodechat.ai.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AiCodeChatService 工厂类
 *
 * @author: <a href="https://gitee.com/Harmony_TL">harmony</a>
 * @DateTime: 2026-03-02
 */
@Configuration
public class AiCodeChatServiceFactory {

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private ChatModel myQwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

//    @Resource
//    private McpToolProvider mcpToolProvider;

    @Bean
    public AiCodeChatService aiCodeHelperService() {
        // 会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造 AI Service
        return AiServices.builder(AiCodeChatService.class)
                .chatModel(myQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.withMaxMessages(10)) // 每个会话独立存储
                .contentRetriever(contentRetriever) // RAG 检索增强生成
//                .tools(new InterviewQuestionTool()) // 工具调用
//                .toolProvider(mcpToolProvider) // MCP 工具调用
                .build();
    }
}
