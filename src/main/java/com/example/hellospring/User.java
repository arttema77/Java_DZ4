package com.example.hellospring;

import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String displayName;
    private String email;

    public User() {}
    public User(String username, String displayName, String email) {
        this.username = username; this.displayName = displayName; this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override public boolean equals(Object o){ return o instanceof User u && Objects.equals(id,u.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
}
