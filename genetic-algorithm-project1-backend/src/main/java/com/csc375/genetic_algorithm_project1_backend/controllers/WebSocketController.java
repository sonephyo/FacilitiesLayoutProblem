package com.csc375.genetic_algorithm_project1_backend.controllers;

import com.csc375.genetic_algorithm_project1_backend.models.StartRequest;
import com.csc375.genetic_algorithm_project1_backend.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@CrossOrigin("http://localhost:3000")
public class WebSocketController {

    private final WebSocketService webSocketService;

    @Autowired
    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/start")
    public void startScheduler(@Payload StartRequest startRequest) throws InterruptedException {
        webSocketService.generateFLPSolution(startRequest);
    }

    @MessageMapping("/terminate")
    public void stopScheduler() {
        webSocketService.terminateProgram();
    }


}
