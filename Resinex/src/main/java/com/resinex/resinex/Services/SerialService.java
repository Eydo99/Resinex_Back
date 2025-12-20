package com.resinex.resinex.Services;

import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

@Service
public class SerialService {

    private SerialPort serialPort ;
    private boolean readerStarted=false;

    @Autowired
    private MotionService motionService;

    @PostConstruct
    public void init(){
        serialPort = SerialPort.getCommPort("COM7");
        serialPort.setBaudRate(9600);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if(serialPort.openPort()){
            System.out.println("CONNECTED SUCCESSFULLY   : " + serialPort.getSystemPortName());
            try {
                Thread.sleep(2000);

                if(!readerStarted)
                {
                    startReader();
                    readerStarted=true;
                }

            }
            catch(Exception ignored){}
        }
        else {
            System.out.println("FAILED TO CONNECT");
        }
    }

    public synchronized void sendToSerial(String message) {
        try {
            if (serialPort == null || !serialPort.isOpen()) {
                System.out.println("Port not open. Reconnecting...");
                init();
                if (!serialPort.isOpen()) return;
            }

            OutputStream output = serialPort.getOutputStream();
            output.write((message + "\n").getBytes());
            output.flush();

            System.out.println("→ Arduino: " + message);
        }

        catch (IOException e) {e.printStackTrace();}
    }

    private void startReader() {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(serialPort.getInputStream())) {
                while (serialPort.isOpen()) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        handleLine(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "arduino-serial-reader").start();
    }

    private void handleLine(String line) {
        System.out.println("← Arduino: " + line);
        if(line.startsWith("EVENT:MOTION:"))
        {
            motionService.onMotionDetected(line.substring("EVENT:MOTION:".length()));
        }
    }
}
