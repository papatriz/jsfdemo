package com.papatriz.jsfdemo.events;

public class TruckTableChangedEvent {

    private String message;
    public TruckTableChangedEvent(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
