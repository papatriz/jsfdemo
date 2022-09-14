package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.ETruckStatus;
import com.papatriz.jsfdemo.models.Truck;
import com.papatriz.jsfdemo.services.ITruckService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Scope(value = "session")
@Component(value = "fleetController")
@ELBeanName(value = "fleetController")
@Join(path = "/fleet", to = "/fleet.xhtml")
public class FleetController {
    private final ITruckService truckService;
    @Autowired
    public FleetController(ITruckService truckService) {
        this.truckService = truckService;
    }

    private List<Truck> fleet;

    private Map<ETruckStatus, String> statusColor =
            Map.of(ETruckStatus.AVAILABLE, "DarkGreen", ETruckStatus.BUSY, "DarkOrange", ETruckStatus.BROKEN, "DarkRed");

    @Deferred
    @RequestAction
    @IgnorePostback
   // @PostConstruct
    public void loadData(){
        System.out.println(("Fleet loadData executed"));

        fleet = truckService.getAllTrucks();
    }

    public List<Truck> getFleet() {
        return fleet;
    }

    public String getColorByStatus(ETruckStatus status) {
        return "color:"+statusColor.get(status);
    }
}
