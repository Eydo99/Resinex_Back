package com.resinex.resinex.Services;

import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.IOException;

@Service
public class LightService {
    private SerialPort serialPort ;

    @PostConstruct
    public void initt(){
        serialPort = SerialPort.getCommPort("COM7");
        serialPort.setBaudRate(9600);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if(serialPort.openPort()){
            System.out.println("CONNECTED SUCCESSFULLY   : " + serialPort.getSystemPortName());
            try { Thread.sleep(2000); } catch(Exception e){}
        }
        else {
            System.out.println("FAILED TO CONNECT");
        }
    }

    public void sendToSerial(String room , int intensity){
        if (serialPort == null || !serialPort.isOpen()) {
            System.out.println("Port not open. Reconnecting...");
            initt();
            if (!serialPort.isOpen()) return;
        }
        try{
            String message = room + ":" + intensity + "\n";
            System.out.println(message);
            OutputStream output = serialPort.getOutputStream();
            output.write(message.getBytes());
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}