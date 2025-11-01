package com.example.hellospring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service){ this.service = service; }

    @GetMapping
    public List<Message> getMessages(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(name="author", required = false) String authorUsername){
        return service.getPage(page, size, authorUsername);
    }

    @GetMapping("/{id}")
    public Message getById(@PathVariable Long id){
        return service.getById(id);
    }

    public record CreateMessageRequest(String content, String authorUsername){}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Message create(@RequestBody CreateMessageRequest req){
        return service.create(req.content(), req.authorUsername());
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public int[] createBatch(@RequestBody List<Message> messages){
        return service.createBatch(messages);
    }
}
