package com.harmony.aicodechat.ai;

import com.harmony.aicodechat.ai.guardrail.SafeInputGuardrail;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;

import java.util.List;

/**
 * AiCodeChatService 接口
 *
 * @author: <a href="https://gitee.com/Harmony_TL">harmony</a>
 * @DateTime: 2026-02-28
 */
@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeChatService {
    @SystemMessage(fromResource = "prompt/system-prompt.txt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "prompt/system-prompt.txt")
    Report chatForReport(String userMessage);

    // 学习报告
    record Report(@Description("用户名称") String name,@Description("学习建议") List<String> suggestionList){}

    @SystemMessage(fromResource = "prompt/system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

}
