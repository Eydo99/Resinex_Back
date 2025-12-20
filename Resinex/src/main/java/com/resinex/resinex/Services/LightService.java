package com.resinex.resinex.Services;

import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.IOException;

@Service
public class LightService {

    @Autowired
    private SerialService serialService;


    public void sendToSerial(String room , int intensity) {
       serialService.sendToSerial(room+":"+intensity);
    }
}