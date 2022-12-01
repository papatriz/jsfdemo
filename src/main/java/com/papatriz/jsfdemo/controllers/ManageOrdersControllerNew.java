package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.annotations.CustomProfile;
import com.papatriz.jsfdemo.events.TruckTableChangedEvent;
import com.papatriz.jsfdemo.exceptions.NoLoadCargoPointException;
import com.papatriz.jsfdemo.models.main.*;
import com.papatriz.jsfdemo.services.IDriverService;
import com.papatriz.jsfdemo.services.IOrderService;
import com.papatriz.jsfdemo.services.ITruckService;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

@Scope(value = "session")
@Component(value = "ordersController")
@ELBeanName(value = "ordersController")
@Join(path = "/manage_orders_new", to = "/manage_orders_new.xhtml")
@Data
@CustomProfile
public class ManageOrdersControllerNew implements IManageOrdersControllerNew {

    private  final IOrderService orderService;
    private  final ITruckService truckService;
    private  final IDriverService driverService;
    @Autowired
    public ManageOrdersControllerNew(IOrderService orderService, ITruckService truckService, IDriverService driverService) {
        this.orderService = orderService;
        this.truckService = truckService;
        this.driverService = driverService;
    }

    private Order newOrder;
    private List<Truck> cachedTrucks;
    private List<Order> cachedPendingOrders;
    private List<Order> cachedActiveOrders;
    private List<CargoCycle> cargoCycles;
    private boolean needUpdate;
    private Logger logger = LoggerFactory.getLogger(ManageOrdersControllerNew.class);

    @PostConstruct
    void init() {
        loadData();
        initNewOrder();
    }
    void initNewOrder() {
        newOrder = new Order();
        cargoCycles = new ArrayList<>();
        CargoCycle currentCargoCycle = new CargoCycle();
        cargoCycles.add(currentCargoCycle);
    }

    void loadData() {
        cachedTrucks = truckService.getAllTrucks();
        cachedPendingOrders = orderService.getPendingOrders();
        for (Order o : cachedPendingOrders) {
            o.setMaxWeight(orderService.getOrderMaxWeight(o));
            o.setDrivers(Collections.emptyList());
        }
        cachedActiveOrders = orderService.getActiveOrders();
        for (Order o : cachedActiveOrders) {
            //    o.setMaxWeight(getOrderTotalWeight(o));
            logger.info("Order ID:" + o.getId() + " Drivers num: " + o.getDrivers().size());
        }
    }
    @Override
    @EventListener
    public void afterFleetChanged(TruckTableChangedEvent event) {
        logger.info("onTruckTableChanged: "+event.getMessage());
        logger.info(Thread.currentThread().getName());
        needUpdate = true;
    }

    @Override
    public List<Truck> getSuitableTrucks(Order order) {

        if (needUpdate) {
            cachedTrucks = truckService.getAllTrucks();
            needUpdate = false;
            logger.info("Cached trucks refreshed");
        }
        int maxWeight = order.getMaxWeight();
        return cachedTrucks.stream()
                .filter(truck -> truck.isAvailable() && (truck.getCapacity() >= maxWeight))
                             .collect(Collectors.toList());
    }

    @Override
    public void onTruckSelect(Truck truck) {

        showMessage("Truck changed, "+truck.toString(), true);
    }

    @Override
    public void onSelectCheckboxChange(Order o) {

        logger.info("On checkbox change: "+o.getDrivers().size());
        o.getDrivers().stream().forEach(d -> logger.info(" : "+d.toString()));
        showMessage("onDriverSelect: drivers num = "+o.getDrivers().size(), true);
    }

    @Override
    public List<Driver> getSuitableDrivers(Order order) {
        // toDO: implement ICountry and use it for time estimation
        int estimatedOrderTime = 8;

        return driverService.getVacantDrivers().stream()
                .filter(driver -> driver.getWorkingHours() < (driverService.getMaxWorkHours() - estimatedOrderTime))
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrder(Order order) {
        order.getAssignedTruck().setOrder(order);
        order.getAssignedTruck().setAssignedDrivers(order.getDrivers());
        truckService.saveTruck(order.getAssignedTruck());
        order.getDrivers().stream().forEach(driver ->
        {
            driver.setOrder(order);
            driver.setCurrentTruck(order.getAssignedTruck());
            driver.setStatus(EDriverStatus.ASSIGNED);
            driverService.saveDriver(driver);
        });

        orderService.saveOrder(order);
        loadData();
        showMessage("Order saved and active now", true);
    }
    @Override
    public void addOrder() throws NoLoadCargoPointException {

        List<Node> orderNodes = new ArrayList<>();
        cargoCycles.stream().forEach(cargoCycle -> cargoCycle.setHasCitiesError(false));
        boolean hasError = false;

        for (CargoCycle cc:cargoCycles) {
            // final check for different pickup/delivery points to handle user case then user change city in already added node
            if (cc.getLoadNode().getCity() == cc.getUnloadNode().getCity()) {
                cc.setHasCitiesError(true);
                hasError = true;
            }
            cc.getLoadNode().setOrder(newOrder);
            cc.getUnloadNode().setOrder(newOrder);
            cc.getCargo().setStatus(ECargoStatus.PREPARED);
            orderNodes.add(cc.getLoadNode());
            orderNodes.add(cc.getUnloadNode());
        }

        if (hasError) {
            showMessage("Delivery has to be in different city");
            return;
        }

        newOrder.setNodes(orderNodes);
        orderService.makeWayBill(newOrder);

        newOrder.setMaxWeight(orderService.getOrderMaxWeight(newOrder));

        if (newOrder.getMaxWeight() > getTruckMaxCapacity()) {
            showMessage("Maximum load ("+newOrder.getMaxWeight()+" kg) exceed truck max payload ("+getTruckMaxCapacity()+" kg)");
            return;
        }

        orderService.saveOrder(newOrder);
        initNewOrder();
        loadData();
        showMessage("New order added", true);
    }


    @Override
    public void addCargo() {

        if (!cargoCycles.isEmpty()) {

            CargoCycle lastCC = cargoCycles.get(cargoCycles.size() - 1);
            lastCC.setHasWeightError(false);
            lastCC.setHasCitiesError(false);

            // check for different load/unload points
            if (lastCC.getLoadNode().getCity() == lastCC.getUnloadNode().getCity()) {
                showMessage("Delivery has to be in different city");
                lastCC.setHasCitiesError(true);
                return;
            }
            // check for single cargo max weight
            if (lastCC.getCargo().getWeight() > getTruckMaxCapacity()) {
                showMessage("Cargo weight exceed max truck capacity");
                lastCC.setHasWeightError(true);
                return;
            }
        }

        CargoCycle currentCargoCycle = new CargoCycle();
        cargoCycles.add(currentCargoCycle);
    }


    @Override
    public void removeCargo(CargoCycle cs) {
        cargoCycles.remove(cs);
    }


    @Override
    public int getTruckMaxCapacity() {
       return truckService.getMaxCapacity();
    }

    private void showMessage(String error, boolean... notError) {

        FacesMessage.Severity severity = (notError != null)&&(notError[0])? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, error, ""));
    }

}
