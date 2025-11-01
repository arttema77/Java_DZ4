package com.example.hellospring;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){ super(msg); }
}
