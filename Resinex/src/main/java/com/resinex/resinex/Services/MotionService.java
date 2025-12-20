package com.resinex.resinex.Services;


import org.springframework.stereotype.Service;

@Service
public class MotionService {

    private boolean alarmOn=false;
    private  String activeScope="";


    public void onMotionDetected(String scope)
    {
        if(isAlarmOn()) return;

        alarmOn=true;
        activeScope=scope;
        System.out.println("onMotionDetection in Room"+getActiveScope());

    }

    public void acknowledgeAlarm()
    {
        if(!isAlarmOn()) return;
        alarmOn=false;
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
