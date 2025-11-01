package com.example.hellospring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private final JdbcMessageRepository msgRepo;
    private final JdbcUserRepository userRepo;

    public MessageService(JdbcMessageRepository msgRepo, JdbcUserRepository userRepo){
        this.msgRepo = msgRepo; this.userRepo = userRepo;
        initializeSampleData();
    }

    @Transactional
    void initializeSampleData(){
        msgRepo.clearAll();
        var system = userRepo.save(new User("system","System","system@example.com"));
        var admin  = userRepo.save(new User("admin","Admin","admin@example.com"));
        msgRepo.save(new Message("Hello, JDBC Spring!", system.getId()));
        msgRepo.save(new Message("Now using real DB via JdbcTemplate.", admin.getId()));
    }

    public Message getById(Long id){
        return msgRepo.findById(id).orElseThrow(() -> new NotFoundException("Message id="+id+" not found"));
    }

    public List<Message> getPage(Integer page, Integer size, String authorUsername){
        int p = page==null?0:Math.max(0,page);
        int s = size==null?30:Math.min(Math.max(1,size),200);
        return (authorUsername!=null && !authorUsername.isBlank())
                ? msgRepo.findByAuthorUsername(authorUsername, p, s)
                : msgRepo.findPage(p, s);
    }

    @Transactional
    public Message create(String content, String authorUsername){
        if (content==null || content.isBlank()) throw new IllegalArgumentException("content must not be blank");
        if (authorUsername==null || authorUsername.isBlank()) throw new IllegalArgumentException("authorUsername must not be blank");
        Long authorId = userRepo.findByUsername(authorUsername)
                .orElseThrow(() -> new NotFoundException("Author '"+authorUsername+"' not found"))
                .getId();
        return msgRepo.save(new Message(content, authorId));
    }

    @Transactional
    public int[] createBatch(List<Message> messages){
        if (messages==null || messages.isEmpty()) return new int[0];
        for (Message m : messages){
            if (m.getContent()==null || m.getContent().isBlank()) throw new IllegalArgumentException("content must not be blank");
            if (m.getAuthorUsername()==null || m.getAuthorUsername().isBlank()) throw new IllegalArgumentException("authorUsername must not be blank");
            Long authorId = userRepo.findByUsername(m.getAuthorUsername())
                    .orElseThrow(() -> new NotFoundException("Author '"+m.getAuthorUsername()+"' not found"))
                    .getId();
            m.setAuthorId(authorId);
        }
        return msgRepo.saveBatch(messages);
    }
}
