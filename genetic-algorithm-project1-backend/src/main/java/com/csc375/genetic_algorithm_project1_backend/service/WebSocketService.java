package com.csc375.genetic_algorithm_project1_backend.service;

import com.csc375.genetic_algorithm_project1_backend.facilityLayoutProblemSolution.Layout;
import com.csc375.genetic_algorithm_project1_backend.models.StartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void generateFLPSolution(StartRequest startRequest) throws InterruptedException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("status", "start");
        messagingTemplate.convertAndSend("/topic/status", data);

        Layout layout = new Layout(startRequest.getNumberOfThreads(), this);
        layout.evaluate(startRequest.getNumberOfStations(), startRequest.getCountOfGAOperations());
    }

    public synchronized void sendData(int[][] generatedData, double affinity_value, int operationNumber) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("data", generatedData);
            data.put("affinity_value", affinity_value);
            data.put("operationNumber", operationNumber);

            // Send the message through the WebSocket
            messagingTemplate.convertAndSend("/topic/reply", data);
    }

    public void sendFinish() {

        HashMap<String, Object> data = new HashMap<>();
        data.put("status", "end");
        messagingTemplate.convertAndSend("/topic/status", data);
    }

    public void terminateProgram() {
        System.exit(0);
    }

}
