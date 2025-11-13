package com.example.hellospring;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // "Плохой" вариант — вызовет N+1 при LAZY
    List<Message> findTop30ByOrderByCreatedAtDesc();

    // Оптимизированный вариант: один запрос с join fetch через EntityGraph
    @EntityGraph(attributePaths = "author")
    List<Message> findTop30WithAuthorByOrderByCreatedAtDesc();

    // Для решения LazyInitializationException c DTO
    @EntityGraph(attributePaths = "author")
    Optional<Message> findWithAuthorById(Long id);
}
