package com.example.hellospring;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private String authorDisplayName;
    private LocalDateTime createdAt;

    public Message() {}
    public Message(String content, Long authorId) {
        this.content = content; this.authorId = authorId;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getContent(){ return content; }
    public void setContent(String content){ this.content = content; }
    public Long getAuthorId(){ return authorId; }
    public void setAuthorId(Long authorId){ this.authorId = authorId; }
    public String getAuthorUsername(){ return authorUsername; }
    public void setAuthorUsername(String authorUsername){ this.authorUsername = authorUsername; }
    public String getAuthorDisplayName(){ return authorDisplayName; }
    public void setAuthorDisplayName(String authorDisplayName){ this.authorDisplayName = authorDisplayName; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    @Override public boolean equals(Object o){ return o instanceof Message m && Objects.equals(id,m.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
}
