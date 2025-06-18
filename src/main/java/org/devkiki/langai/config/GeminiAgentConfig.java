package org.devkiki.langai.config;


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
        return AiServices.builder(BookingAssistant.class)
                .chatModel(gemini)
                .tools(bookingTool)
                .build();
    }

}
