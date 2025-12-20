package com.resinex.resinex.Services;


import com.resinex.resinex.Events.MotionDetectedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class MotionService {

    private boolean alarmOn=false;
    private  String activeScope="";

    @Autowired
    private AlertStreamService alertStreamService;

    @Autowired
    private SerialService serialService;

    @EventListener
    public void onMotionEvent(MotionDetectedEvent event) {
        onMotionDetected(event.getScope());
    }

    public void onMotionDetected(String scope)
    {
        if(isAlarmOn()) return;

        alarmOn=true;
        activeScope=scope;
        alertStreamService.send("motion",getActiveScope());
        System.out.println("onMotionDetection in Room"+getActiveScope());

    }

    public void acknowledgeAlarm()
    {
        if(!isAlarmOn()) return;

        serialService.sendToSerial("ACK:MOTION:"+getActiveScope());
        alertStreamService.send("alarmCleared",getActiveScope());

        System.out.println("Alarm acknowledged for " + activeScope);
        
        alarmOn=false;
        activeScope="";
    }

    private boolean isAlarmOn()
    {
        return alarmOn;
    }

    private String  getActiveScope()
    {
        return activeScope;
    }

}
