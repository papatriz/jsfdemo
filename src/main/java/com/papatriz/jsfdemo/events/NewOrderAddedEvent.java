package com.papatriz.jsfdemo.events;

public class NewOrderAddedEvent {
    private String message;
    public NewOrderAddedEvent(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
