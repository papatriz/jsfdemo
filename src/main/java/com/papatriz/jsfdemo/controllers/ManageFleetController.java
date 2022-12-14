package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.main.ETruckStatus;
import com.papatriz.jsfdemo.models.main.Truck;
import com.papatriz.jsfdemo.services.ITruckService;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ToggleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Scope(value = "session")
@Component(value = "fleetController")
@ELBeanName(value = "fleetController")
@Join(path = "/fleet", to = "/manage_fleet.xhtml")
public class ManageFleetController {
    private final ITruckService truckService;
    @Autowired
    public ManageFleetController(ITruckService truckService) {
        this.truckService = truckService;
    }

    private List<Truck> fleet;
    private List<Truck> filteredTrucks;
    private Truck truck = new Truck();
    private Truck selectedTruck;
    private final Map<ETruckStatus, String> statusColor =
            Map.of(ETruckStatus.AVAILABLE, "DarkGreen", ETruckStatus.BUSY, "DarkOrange", ETruckStatus.BROKEN, "DarkRed");

    @Deferred
    @RequestAction
    @IgnorePostback
   // @PostConstruct
    public void loadData() { fleet = truckService.getAllTrucks(); }

    @PostConstruct
    public void setDefaultDriversNum() {
        truck.setDriversNum(1);
        truck.setCapacity(1000);
    }

    public List<Truck> getFleet() {
        return fleet;
    }

    public void addTruck(){

        truck.setRegNumber(truck.getRegNumber().toUpperCase());
        truckService.saveTruck(truck);

        truck = new Truck();
        setDefaultDriversNum();
        loadData();

        PrimeFaces.current().ajax().update("dataTablePanel");
        PrimeFaces.current().ajax().update("addTruckForm");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New truck added", "New truck added"));
    }

    public void updateTruck(Truck updatedTruck){
        System.out.println("Inside updateTruck");
        updatedTruck.setRegNumber(updatedTruck.getRegNumber().toUpperCase());
        truckService.saveTruck(updatedTruck);

        loadData();

        PrimeFaces.current().ajax().update("dataTablePanel");
        PrimeFaces.current().ajax().update("addTruckForm");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Truck updated", "Truck updated"));
    }

    public void deleteSelectedTruck() {
        System.out.println("DELETE TRUCK "+selectedTruck.getRegNumber());

        truckService.removeTruck(selectedTruck);
        loadData();
        PrimeFaces.current().ajax().update("dataTablePanel");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Truck "+selectedTruck.getRegNumber()+" deleted", "Truck "+selectedTruck.getRegNumber()+" deleted"));

    }

    public void onRowToggle(ToggleEvent event) {
        System.out.println( "Inside onRowToggle in Fleet controller");
    }

    public String getColorByStatus(ETruckStatus status) {
        return "color:"+statusColor.get(status);
    }

    public List<Truck> getFilteredTrucks() {

        return filteredTrucks;
    }

    public void setFilteredTrucks(List<Truck> filteredTrucks) {

        this.filteredTrucks = filteredTrucks;
    }

    public boolean hasMessage() {
        FacesContext context = FacesContext.getCurrentInstance();
        Iterator<FacesMessage> messageQueue =  context.getMessages("messages");

       return messageQueue.hasNext();
    }

    public Truck getTruck() {
        return truck;
    }

    public void setSelectedTruck(Truck selectedTruck) { this.selectedTruck = selectedTruck; }


}
