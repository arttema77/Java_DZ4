package com.example.hellospring;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // Инициализация тестовых данных
    @PostConstruct
    @Transactional
    public void initializeSampleData() {
        if (userRepository.count() > 0) {
            return; // уже инициализировано
        }

        User user1 = userRepository.save(new User("Alice"));
        User user2 = userRepository.save(new User("Bob"));

        messageRepository.save(new Message("Hello, JPA and Hibernate!", user1));
        messageRepository.save(new Message("This uses Spring Data JPA interface.", user2));
        messageRepository.save(new Message("Relationships are now managed by ORM.", user1));
    }

    @Transactional
    public Message saveMessage(String content, String username) {
        User author = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new User(username)));

        Message message = new Message(content, author);
        return messageRepository.save(message);
    }

    /**
     * Вариант A: демонстрация N+1.
     * Один запрос за 30 сообщений + N запросов за авторов.
     */
    @Transactional
    public List<Message> getLatestMessagesNPlus1() {
        System.out.println("--- ЗАПРОС N+1 ---");
        List<Message> messages = messageRepository.findTop30ByOrderByCreatedAtDesc();

        // принудительно трогаем LAZY-поле — Hibernate делает N дополнительных запросов
        messages.forEach(m -> m.getAuthor().getUsername());

        return messages;
    }

    /**
     * Вариант B: оптимизированный запрос.
     * Благодаря @EntityGraph в репозитории всё грузится одним SELECT с JOIN.
     */
    @Transactional
    public List<Message> getLatestMessagesOptimized() {
        System.out.println("--- ОПТИМИЗИРОВАННЫЙ ЗАПРОС (EntityGraph / JOIN FETCH) ---");
        return messageRepository.findTop30WithAuthorByOrderByCreatedAtDesc();
    }

    /**
     * Специально "ломающий" метод для демонстрации LazyInitializationException.
     * Тут НЕТ @Transactional и НЕТ EntityGraph.
     */
    public Message getMessageToFail(Long id) {
        return messageRepository.findById(id).orElseThrow();
    }

    // ====== Способ 1: JPA-способ (просто @Transactional) ======

    @Transactional
    public Message getMessageFixedTransactional(Long id) {
        // внутри активной транзакции LAZY-поле author успеет инициализироваться
        return messageRepository.findById(id).orElseThrow();
    }

    // ====== Способ 2: DTO + EntityGraph (рекомендуемый) ======

    public MessageResponse getMessageAsDto(Long id) {
        // используем метод репозитория с EntityGraph
        Message message = messageRepository.findWithAuthorById(id)
                .orElseThrow();

        // при маппинге в DTO мы трогаем author, но он уже загружен join'ом
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getAuthor().getUsername(),
                message.getCreatedAt()
        );
    }
}
