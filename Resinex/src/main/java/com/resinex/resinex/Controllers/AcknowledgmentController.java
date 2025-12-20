package com.resinex.resinex.Controllers;

import com.resinex.resinex.DTO.AckDTO;
import com.resinex.resinex.Services.MotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AcknowledgmentController {

    @Autowired
    private MotionService motionService;
    @PostMapping("/ack")
    public ResponseEntity<?> ack(@RequestBody AckDTO ack) {
        motionService.acknowledgeAlarm();
        return ResponseEntity.ok().build();
    }
}
