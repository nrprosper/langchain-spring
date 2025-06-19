package org.devkiki.langai.config;


import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.devkiki.langai.BookingAssistant;
import org.devkiki.langai.BookingTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiAgentConfig {
    @Value("${gemini.api_key}")
    private String API_KEY;

    @Bean
    public ChatModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(API_KEY)
                .modelName("gemini-2.5-flash")
                .temperature(0.0)
//                .responseFormat(ResponseFormat.JSON)
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    public BookingAssistant bookingAssistant(BookingTool bookingTool, ChatModel gemini) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
        memory.add(SystemMessage.from("""
                 You are a helpful assistant that helps users book services.
                        Always convert natural language times like "tomorrow at 12pm" into ISO-8601 format (e.g. 2025-06-20T12:00:00) before calling any tool.
                        The 'time' must be in ISO format like 2025-06-20T12:00:00.
                """));
        return AiServices.builder(BookingAssistant.class)
                .chatModel(gemini)
                .tools(bookingTool)
                .build();
    }

}
