package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.events.NewOrderAddedEvent;
import com.papatriz.jsfdemo.message.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Controller
public class MessagingController {
    private final SimpMessagingTemplate messagingTemplate;
    private final Logger logger = LoggerFactory.getLogger(MessagingController.class);
    @Autowired
    public MessagingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void sendNotification(NewOrderAddedEvent event) {

        logger.info("Send update message at " + LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/messages", new Notification("need_update"));
    }
}
