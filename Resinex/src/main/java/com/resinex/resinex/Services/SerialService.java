package com.resinex.resinex.Services;

import com.fazecast.jSerialComm.SerialPort;
import com.resinex.resinex.Events.MotionDetectedEvent;
import com.resinex.resinex.Events.TempEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class SerialService {

    private SerialPort serialPort;
    private boolean readerStarted = false;
    private Thread readerThread;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void init() {
        serialPort = SerialPort.getCommPort("COM5");
        serialPort.setBaudRate(9600);
        // FIXED: Changed from TIMEOUT_WRITE_BLOCKING to TIMEOUT_NONBLOCKING
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (serialPort.openPort()) {
            System.out.println("CONNECTED SUCCESSFULLY: " + serialPort.getSystemPortName());
            try {
                Thread.sleep(2000);

                if (!readerStarted) {
                    startReader();
                    readerStarted = true;
                }
            } catch (Exception ignored) {}
        } else {
            System.out.println("FAILED TO CONNECT");
        }
    }

    @PreDestroy
    public void cleanup() {
        if (readerThread != null) {
            readerThread.interrupt();
        }
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        readerThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            StringBuilder lineBuilder = new StringBuilder();

            try {
                while (!Thread.currentThread().isInterrupted() && serialPort.isOpen()) {
                    int bytesAvailable = serialPort.bytesAvailable();

                    if (bytesAvailable > 0) {
                        int numRead = serialPort.readBytes(buffer, bytesAvailable);

                        for (int i = 0; i < numRead; i++) {
                            char c = (char) buffer[i];

                            if (c == '\n' || c == '\r') {
                                if (lineBuilder.length() > 0) {
                                    String line = lineBuilder.toString().trim();
                                    handleLine(line);
                                    lineBuilder.setLength(0);
                                }
                            } else {
                                lineBuilder.append(c);
                            }
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                // Thread interrupted, exit gracefully
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "arduino-serial-reader");

        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void handleLine(String line) {
//        System.out.println("← Arduino: " + line);
        if (line.startsWith("EVENT:MOTION:")) {
            String scope = line.substring("EVENT:MOTION:".length());
            publisher.publishEvent(new MotionDetectedEvent(scope));
        }
        if (line.startsWith("TEMP:")) {
            try {
                double temp = Double.parseDouble(line.substring("TEMP:".length()));
                if(temp > 0){
                    publisher.publishEvent(new TempEvent(temp));
                    System.out.println("Temp temperature: " + temp);
                }

            } catch (NumberFormatException ignored) {}
        }
    }
}