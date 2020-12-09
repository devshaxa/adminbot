package uz.softex.adminbot.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Notification;

@Service
public class NotificationService {

    private SimpMessagingTemplate simpMessagingTemplate;

    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void notify(Notification notification, String username) {
        simpMessagingTemplate.convertAndSendToUser(
                username,
                "/queue/notify",
                notification
        );
        return;
    }
}
