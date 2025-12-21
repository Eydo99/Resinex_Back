package com.resinex.resinex.Controllers;

import com.resinex.resinex.DTO.AckDTO;
import com.resinex.resinex.Services.MotionService;
import com.resinex.resinex.Services.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AcknowledgmentController {

    @Autowired
    private MotionService motionService;
    @Autowired
    private TempService tempService;
    @PostMapping("/ack")
    public ResponseEntity<?> ack() {
        System.out.println("AcknowledgmentController - ack");
        motionService.acknowledgeAlarm();
        return ResponseEntity.ok().build();
    }
    //Temperature SAFE
    @PostMapping("/temperature/ack")
    public ResponseEntity<?> acknowledgeTemperature() {
        tempService.acknowledgeAlarm();
        return ResponseEntity.ok().build();
    }

    //Resume temperature monitoring
    @PostMapping("/temperature/resume")
    public ResponseEntity<?> resumeTemperature() {
        tempService.resumeMonitoring();
        return ResponseEntity.ok().build();
    }
}
