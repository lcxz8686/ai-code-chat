package com.harmony.aicodechat.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeChatServiceFactoryTest {
    @Resource
    private AiCodeChatService aiCodeChatService;

    @Test
    void chat() {
        String result = aiCodeChatService.chat("你好，我是你爸爸");
        System.out.println("result:" + result);

        String result1 = aiCodeChatService.chat("我刚刚说了什么");
        System.out.println("result1:" + result1);
    }

    @Test
    void chatForReport() {
        String userMessage = "你好，我是程序员鱼皮，3岁，学编程两年半，请帮我制定学习报告";
        AiCodeChatService.Report result = aiCodeChatService.chatForReport(userMessage);
        System.out.println("result:" + result);
    }

    @Test
    void chatWithRag() {
        Result<String> result = aiCodeChatService.chatWithRag("怎么学习 Java？有哪些常见面试题？");
        System.out.println(result.content());
        System.out.println(result.sources());
    }

}