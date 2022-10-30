package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.security.UserDetailsImpl;
import com.papatriz.jsfdemo.services.IDriverService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    @Autowired
    private final IDriverService driverService;
    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
    }
    private Logger logger = LoggerFactory.getLogger(DriverController.class);
    private Driver driver;
    private ICountry country = new Country();

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
        Node node = driver.getOrder().getNodes().stream().filter(n -> !n.isComplete()).findFirst().orElse(null);

        return node.getCity()+" : "+node.getCargo().getName()+" ("+node.getCargo().getWeight()+" kg) - "+node.getType();
    }

    public List<Node> getWayPoints() throws Exception {

        List<Node> origin = driver.getOrder().getNodes();
        origin.sort(new ManageOrdersControllerNew.CityBasedComparator());

        boolean restartCycle = false;

        for (int i=0; i < origin.size()-1; i++) {
            if (restartCycle) {
                i=0;
                restartCycle = false;
            }
            if(origin.get(i).getType() == EActionType.UNLOAD) {
                List<Node> previousNodes = (i > 0)? origin.subList(0, i) : new ArrayList<>();
                Cargo checkedCargo = origin.get(i).getCargo();
                boolean wasLoaded = previousNodes.stream().anyMatch(ni -> ni.getCargo().equals(checkedCargo));

                if (!wasLoaded) {
                    List<Node> nextNodes = origin.subList(i+1, origin.size());
                    Optional<Node> loadNode = nextNodes.stream().filter(n -> n.getCargo().equals(checkedCargo)).findFirst();
                    int loadIndex = origin.indexOf(loadNode.get());
                    Node tmpNode = origin.get(i);
                    origin.remove(tmpNode);
                    origin.add(loadIndex, tmpNode);
                    ECity lastCity = loadNode.get().getCity();
                    origin.subList(loadIndex, origin.size()).sort((o1, o2) -> {
                        if (o1.getCity() == o2.getCity()) return 0;
                        int distance1 = country.getDistance(o1.getCity(), lastCity);
                        int distance2 = country.getDistance(o2.getCity(), lastCity);
                        return (distance1 > distance2) ? 1 : -1;
                    });
                    restartCycle = true;
                }
            }
        }
        for (Node n:origin) {
            logger.info(n.toString());
        }
        return origin;
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
}
