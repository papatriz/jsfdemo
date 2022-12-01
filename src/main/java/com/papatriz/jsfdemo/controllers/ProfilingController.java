package com.papatriz.jsfdemo.controllers;

import org.springframework.beans.factory.annotation.Value;

public class ProfilingController {
    @Value("${profiling.enabled}")
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
