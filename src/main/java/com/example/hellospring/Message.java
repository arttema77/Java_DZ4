package com.example.hellospring;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)  // важно LAZY для демонстрации проблем
    @JoinColumn(name = "author_id")
    private User author;

    protected Message() {
        // JPA конструктор
    }

    public Message(String content, User author) {
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // getters / setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getAuthor() { return author; }

    public void setAuthor(User author) { this.author = author; }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
