package com.resinex.resinex.Controllers;

import com.resinex.resinex.Services.AlertStreamService;
import com.resinex.resinex.Services.MotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertStreamController {

    @Autowired
    private AlertStreamService alertStreamService;

    @Autowired
    private MotionService motionService;

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = alertStreamService.subscribe();

        // Send current alarm state to new subscriber
        // This ensures refreshed pages get the current state
        if (motionService.isAlarmOn()) {
            try {
                Map<String, Object> currentState = new HashMap<>();
                currentState.put("scope", motionService.getActiveScope());
                currentState.put("isActive", true);
                currentState.put("timestamp", System.currentTimeMillis());

                emitter.send(
                        SseEmitter.event()
                                .name("motion")
                                .data(currentState));
                System.out.println("[AlertStream] Sent current alarm state to new subscriber");
            } catch (Exception e) {
                System.err.println("[AlertStream] Failed to send initial state: " + e.getMessage());
            }
        }

        return emitter;
    }
}
