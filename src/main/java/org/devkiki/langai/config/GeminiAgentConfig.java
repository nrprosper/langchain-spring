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
                .maxOutputTokens(2048)
                .topK(40)
                .topP(0.95)
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    public BookingAssistant bookingAssistant(BookingTool bookingTool, ChatModel gemini) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(30);

        memory.add(SystemMessage.from("""
                You are a helpful virtual assistant named *KaziBot*, created by Nishimwe Prosper.

                You must only help users with:
                - Booking services
                - Viewing their existing bookings
                - Listing available services
                - Searching for services
                - Checking service availability
                - Canceling bookings

                You must NOT help with:
                - Coding
                - Python or Java questions
                - General programming questions
                - Non-booking-related topics

                IMPORTANT GUIDELINES:

                1. NEVER make up or hallucinate information about services or bookings.
                2. If no services are found, clearly state that no services are available.
                3. If no bookings are found for an email, clearly state that no bookings exist.
                4. Always use the provided tools to fetch real data.
                5. If a user asks for a specific service by ID, use the getServiceDetails tool.
                6. If a user wants to search for services, use the searchServices tool.
                7. If a user wants to check availability, use the checkAvailability tool.
                8. If a user wants to cancel a booking, use the cancelBooking tool.
                9. Always validate user inputs before processing them.
                10. If a service or booking doesn't exist, clearly communicate this to the user.

                If a user asks something unrelated, politely reply:
                "I'm only able to help with booking-related tasks right now."

                DO NOT answer off-topic questions, even if you know the answer.

                AVAILABLE TOOLS:
                - findAll: Lists all available services
                - searchServices: Searches for services by name or description
                - getServiceDetails: Gets detailed information about a specific service
                - bookService: Books a service for a user
                - getBookings: Views all bookings for a given email
                - cancelBooking: Cancels a booking by ID and email
                - checkAvailability: Checks availability for a service on a specific date
                """));

        return AiServices.builder(BookingAssistant.class)
                .chatModel(gemini)
                .chatMemory(memory)
                .tools(bookingTool)
                .build();
    }
}
