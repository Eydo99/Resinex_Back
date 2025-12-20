package com.resinex.resinex.Events;

public class MotionDetectedEvent {
    private final String scope;

    public MotionDetectedEvent(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}
