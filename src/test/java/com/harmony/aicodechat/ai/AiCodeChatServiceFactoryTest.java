package com.harmony.aicodechat.ai;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        Result<String> result = aiCodeChatService.chatWithRag("Java程序员在 MySQL方面 需要关注什么核心的面试也题目？");
        String content = result.content();
//        List<Content> sources = result.sources();
//        for (Content source : sources) {
//            System.out.println("===========");
//            System.out.println(source.metadata());
//            System.out.println(source.textSegment().text());
//            System.out.println("===========");
//        }
        System.out.println(content);
//        System.out.println(sources);
    }

    @Test
    void chatWithMcp() {
        String result = aiCodeChatService.chat("什么是程序员鱼皮的编程导航？");
        System.out.println(result);
    }




}