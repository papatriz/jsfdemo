package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.security.UserDetailsImpl;
import com.papatriz.jsfdemo.services.IDriverService;
import com.papatriz.jsfdemo.services.IOrderService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Scope(value = "session")
@Component(value = "driverController")
@ELBeanName(value = "driverController")
@Join(path = "/driver", to = "/driver.xhtml")
public class DriverController {
    private final IDriverService driverService;
    private final IOrderService orderService;
    @Autowired
    public DriverController(IDriverService driverService, IOrderService orderService) {
        this.driverService = driverService;
        this.orderService = orderService;
    }
    private final Logger logger = LoggerFactory.getLogger(DriverController.class);
    private Driver driver;

    @PostConstruct
    private void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        driver = driverService.getByUserId(principal.getId()).orElse(null);
    }

    public List<Driver> getCoDrivers() {
        List<Driver> list = driver.getOrder().getDrivers();

        return list.stream().filter(other -> !other.equals(driver)).collect(Collectors.toList());
    }

    public String getCurrentTargetPoint() {
        Optional<Node> node = driver.getOrder().getNodes().stream().filter(n -> !n.isComplete()).findFirst();
        if (node.isEmpty()) return "Order complete";

        return node.get().getCity()+" : "+node.get().getCargo().getName()+" ("+node.get().getCargo().getWeight()+" kg) - "+node.get().getType();
    }

    public List<Node> getWayPoints() {

        return driver.getOrder().getNodes();
    }

    public boolean isAssigned() {
        return driver.getStatus() == EDriverStatus.ASSIGNED;
    }

    public boolean isConfirmed() {
        return driver.getStatus() == EDriverStatus.CONFIRM;
    }

    public void confirmOrder() {
        logger.info("Set driver status to CONFIRM");
        driver.setStatus(EDriverStatus.CONFIRM);
        driverService.saveDriver(driver);
    }

    public Driver getDriver() {
        return driver;
    }

    public void setWayPointComplete() {
        Optional<Node> node = driver.getOrder().getNodes().stream().filter(n -> !n.isComplete()).findFirst();
        if (node.isEmpty()) {
            // Order complete, do some things
            driver.getOrder().setComplete(true);
            orderService.saveOrder(driver.getOrder());
            return;
        }
        node.get().setComplete(true);
        orderService.saveOrder(driver.getOrder());
    }
}
