package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.repository.MessageRepository;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
