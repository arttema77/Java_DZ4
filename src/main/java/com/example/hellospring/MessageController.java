package com.example.hellospring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DTO для входящего запроса
record CreateMessageRequest(String content, String username) {}

@RestController
@RequestMapping("/messages") // вместе с context-path /api -> /api/messages
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    // ----- N+1 и оптимизированный запрос -----

    // GET /api/messages/nplus1
    @GetMapping("/nplus1")
    public List<Message> getMessagesNPlus1() {
        return service.getLatestMessagesNPlus1();
    }

    // GET /api/messages/optimized
    @GetMapping("/optimized")
    public List<Message> getMessagesOptimized() {
        return service.getLatestMessagesOptimized();
    }

    // ----- Создание сообщения -----

    // POST /api/messages
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Message createMessage(@RequestBody CreateMessageRequest request) {
        String username = (request.username() == null || request.username().trim().isEmpty())
                ? "Anonymous"
                : request.username();

        return service.saveMessage(request.content(), username);
    }

    // ----- Демонстрация LazyInitializationException -----
    // GET /api/messages/fail/{id}
    @GetMapping("/fail/{id}")
    public ResponseEntity<?> getMessageToFail(@PathVariable Long id) {
        try {
            Message message = service.getMessageToFail(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String errorMsg = "Ошибка! Скорее всего LazyInitializationException " +
                    "(Proxy Exception), потому что LAZY-поле author " +
                    "пытаются сериализовать вне транзакции. " +
                    "Попробуйте варианты /fixed-tx/{id} или /dto/{id}.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    // ----- Способ 1: JPA (@Transactional в сервисе) -----
    // GET /api/messages/fixed-tx/{id}
    @GetMapping("/fixed-tx/{id}")
    public Message getMessageFixedTransactional(@PathVariable Long id) {
        return service.getMessageFixedTransactional(id);
    }

    // ----- Способ 2 (рекомендуемый): DTO + EntityGraph -----
    // GET /api/messages/dto/{id}
    @GetMapping("/dto/{id}")
    public MessageResponse getMessageAsDto(@PathVariable Long id) {
        return service.getMessageAsDto(id);
    }
}
