package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.main.Driver;
import com.papatriz.jsfdemo.models.main.EDriverStatus;
import com.papatriz.jsfdemo.models.auth.User;
import com.papatriz.jsfdemo.services.IDriverService;
import com.papatriz.jsfdemo.services.UserService;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

@Data
@Scope(value = "session")
@Component(value = "manageDriversController")
@ELBeanName(value = "manageDriversController")
@Join(path = "/manage_drivers", to = "/manage_drivers.xhtml")
public class ManageDriversController {

    private final IDriverService driverService;
    private final UserService userService;

    @Autowired
    public ManageDriversController(IDriverService driverService, UserService userService) {
        this.driverService = driverService;
        this.userService = userService;
    }

    private Driver driver = new Driver();
    private Driver selectedDriver;

    private List<Driver> driverList;
    private List<Driver> filteredDrivers;

    private final Map<EDriverStatus, String> statusColor =
            Map.of(EDriverStatus.READY, "DarkGreen", EDriverStatus.ASSIGNED, "DarkBlue", EDriverStatus.REST, "DarkRed");

    @Deferred
    @RequestAction
    @IgnorePostback
    // @PostConstruct
    public void loadData(){

        driverList = driverService.getAllDrivers();
    }

    public void addDriver() {

        String username = driver.getName().toLowerCase()+"."+driver.getSurname().toLowerCase();
        String initPass = "pass";
        User userForDriver = new User(username,initPass, driver.getEmail(), "ROLE_DRIVER");
        userForDriver.setNeedChangePassword(true);
        userService.saveUser(userForDriver);
// TEST TO CREATE NEW USERS WITH HASHED PASSWORDS!!!
        /*
        User userManager = new User("manager",initPass, driver.getEmail(), "ROLE_MANAGER");
        userForDriver.setNeedChangePassword(true);
        userService.saveUser(userManager);

        User userAdmin = new User("admin",initPass, driver.getEmail(), "ROLE_ADMIN");
        userForDriver.setNeedChangePassword(true);
        userService.saveUser(userAdmin);
         */
// =================================================
        driver.setUserId(userForDriver.getId());
        driver.setStatus(EDriverStatus.READY);
        driverService.saveDriver(driver);

        driver = new Driver();
        loadData();

        PrimeFaces.current().ajax().update("driverListPanel");
        PrimeFaces.current().ajax().update("addDriverForm");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New driver added, created user with username "+username+" and default password", "New driver added"));
    }

    private void updateDriver(Driver driver) {

        driverService.saveDriver(driver);
       // loadData();
    }

    public void deleteSelectedDriver() {

        System.out.println( "Inside deleteSelectedDriver in Drivers controller");

        if (selectedDriver.getCurrentTruck() != null)
            selectedDriver.getCurrentTruck().getAssignedDrivers().remove(selectedDriver);

        if (selectedDriver.getOrder() != null)
            selectedDriver.getOrder().getDrivers().remove(selectedDriver);

        driverService.removeDriverById(selectedDriver.getId());

        loadData();
        PrimeFaces.current().ajax().update("driverListPanel");

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Driver "+selectedDriver.getSurname()+" deleted", ""));

    }

    public void setSelectedDriver(Driver selectedDriver) {
        this.selectedDriver = selectedDriver;
        System.out.println("SELECT DRIVER "+selectedDriver.getSurname());
    }

    public void onRowEdit(RowEditEvent<Driver> event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Driver "+ event.getObject().getSurname() +" edited", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        updateDriver(event.getObject());
    }

    public void onRowToggle(ToggleEvent event) {
        System.out.println( "Inside onRowToggle in Drivers controller");

        System.out.println( event.getComponent().getId());
        System.out.println( event.getVisibility());
        System.out.println( event.getData().toString());
    }

    public String getColorByStatus(EDriverStatus status) {
        return "color:"+statusColor.get(status);
    }
}
