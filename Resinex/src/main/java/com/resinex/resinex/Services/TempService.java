package com.resinex.resinex.Services;

import com.resinex.resinex.Events.TempEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class TempService {
    private boolean alarmOn=false; //عشان ميقعدش يولعه كل شوية هى مرة لما يعدى
    private double lastTemp=0;
    private static final double LIMIT = 50.0;

    @Autowired
    private AlertStreamService alertStreamService;

    @Autowired
    private SerialService serialService;

    @EventListener
    public void onTemperatureEvent(TempEvent event) {
        handleTemp(event.getTemperature());
    }
    public void handleTemp(Double temp) {
        this.lastTemp = temp;
        alertStreamService.send("temperature", temp);

        // Alarm logic
        if (temp >= LIMIT && !alarmOn) {
            alarmOn = true;
            alertStreamService.send("tempAlarm", temp);
            System.out.println("Temperature alarm: " + temp);
        }
    }
    public void acknowledgeAlarm() {


        serialService.sendToSerial("SAFE");   // silence Arduino
        alertStreamService.send("tempCleared", lastTemp);

        alarmOn = false;
        System.out.println("Temperature alarm acknowledged");
    }

    public void resumeMonitoring() {
        serialService.sendToSerial("RESUME");
        System.out.println("Temperature monitoring resumed");
    }

}
