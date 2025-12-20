package com.resinex.resinex.Controllers;


import com.resinex.resinex.Services.AlertStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertStreamController {

    @Autowired
    private AlertStreamService alertStreamService;

    @GetMapping("/stream")
    public SseEmitter stream() {
        return alertStreamService.subscribe();
    }
}
