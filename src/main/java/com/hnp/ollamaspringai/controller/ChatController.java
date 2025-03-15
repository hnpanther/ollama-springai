package com.hnp.ollamaspringai.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/chat")
public class ChatController {

    Logger logger = Logger.getLogger(ChatController.class.getName());

    @GetMapping
    public String getChatPage(Model model) {

        logger.info("Entering getChatPage");
        return "chat/chat-page.html";
    }
}
