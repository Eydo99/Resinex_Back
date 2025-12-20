package com.resinex.resinex.Controllers;

import com.resinex.resinex.DTO.LightDTO;
import com.resinex.resinex.Services.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lights")
@CrossOrigin(origins = "http://localhost:4200")
public class LightController {
    @Autowired
    private LightService lightService;

    @PostMapping("/control")
    public void setLight(@RequestBody LightDTO request) {
        System.out.println("Received Web Request: " + request.getRoom() + " == " + request.getIntensity());
        lightService.sendToSerial(request.getRoom(), request.getIntensity());
    }

}
