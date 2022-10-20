package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.Driver;
import com.papatriz.jsfdemo.security.UserDetailsImpl;
import com.papatriz.jsfdemo.services.IDriverService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Scope(value = "session")
@Component(value = "driverController")
@ELBeanName(value = "driverController")
@Join(path = "/driver", to = "/driver.xhtml")
public class DriverController {
    @Autowired
    private final IDriverService driverService;

    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
    }

    public Driver getDriver() {
        return driver;
    }

    private Driver driver;

    @PostConstruct
    private void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        driver = driverService.getByUserId(principal.getId()).orElse(null);
    }
}
