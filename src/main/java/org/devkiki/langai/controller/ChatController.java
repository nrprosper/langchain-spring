package org.devkiki.langai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.BookingAssistant;
import org.devkiki.langai.dto.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Ai Chat Controller", description = "Endpoint to interact with Agentic RAG")
public class ChatController {
    private final BookingAssistant assistant;

    @PostMapping("/chat")
    @Operation(summary = "Interact with Agentic RAG - Just prompt")
    public ResponseEntity<String> chat(@RequestBody ChatRequest chatRequest) {
        String message = chatRequest.message();
        String response = assistant.chat(message);
        return ResponseEntity.ok(response);
    }

}
