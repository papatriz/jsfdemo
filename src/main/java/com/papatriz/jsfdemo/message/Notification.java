package com.papatriz.jsfdemo.message;

import java.io.Serializable;

public class Notification implements Serializable {
    private String content;

    public Notification(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}