package com.resinex.resinex.Events;

public class TempEvent {
    private final Double temperature;

    public TempEvent(Double temperature) {
        this.temperature = temperature;
    }
    public Double getTemperature() {
        return this.temperature;
    }
}
