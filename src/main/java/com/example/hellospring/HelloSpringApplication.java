package com.example.hellospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloSpringApplication {
    public static void main(String[] args) {
        System.setProperty("logging.level.org.springframework.jdbc.core", "DEBUG");
        SpringApplication.run(HelloSpringApplication.class, args);
    }
}
