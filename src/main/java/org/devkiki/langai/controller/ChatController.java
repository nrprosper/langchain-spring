package org.devkiki.langai.controller;

import org.devkiki.langai.BookingAssistant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ChatController {
    private final BookingAssistant assistant;

    public ChatController(BookingAssistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest chatRequest) {
        String message = chatRequest.message();
        String response = assistant.chat(message);
        return ResponseEntity.ok(response);
    }

}
