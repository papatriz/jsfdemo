package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.events.TruckTableChangedEvent;
import com.papatriz.jsfdemo.models.*;
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
public class ManageOrdersControllerNew {

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

    static class CityBasedComparator implements Comparator<Node> { // toDo: move to Node class
        @Override
        public int compare(Node o1, Node o2) { // toDo: after implementation of ICountry make sort on distance base
            if (o1.getCity().equals(o2.getCity()))
            {
                if (o1.getType() == o2.getType()) return 0;
                return o1.getType().ordinal() < o2.getType().ordinal() ? 1 : -1;
            }
            return o1.getCity().ordinal() > o2.getCity().ordinal() ? 1 : -1;
        }
    }

    @PostConstruct
    private void init() {
        loadData();
        initNewOrder();
    }

    @EventListener
    public void afterFleetChanged(TruckTableChangedEvent event) {
        logger.info("onTruckTableChanged: "+event.getMessage());
        logger.info(Thread.currentThread().getName());
        needUpdate = true;
    }

    private void loadData() {
        cachedTrucks = truckService.getAllTrucks();
        cachedPendingOrders = orderService.getPendingOrders();
        for (Order o:cachedPendingOrders) {
            o.setMaxWeight(getOrderTotalWeight(o));
            o.setDrivers(Collections.emptyList());
        }
        cachedActiveOrders = orderService.getActiveOrders();
        for (Order o:cachedActiveOrders) {
        //    o.setMaxWeight(getOrderTotalWeight(o));
            logger.info("Order ID:"+o.getId()+" Drivers num: "+o.getDrivers().size());
        }
    }

    private void initNewOrder() {
        newOrder = new Order();
        cargoCycles = new ArrayList<>();
        CargoCycle currentCargoCycle = new CargoCycle();
        cargoCycles.add(currentCargoCycle);
    }

    public List<Truck> getSuitableTrucks(Order order) {

        if (needUpdate) {
            cachedTrucks = truckService.getAllTrucks();
            needUpdate = false;
            logger.info("Cached trucks refreshed");
        }
        int maxWeight = order.getMaxWeight();
        List<Truck> suitableTrucks =
                cachedTrucks.stream().filter(truck -> truck.isAvailable() && (truck.getCapacity() >= maxWeight))
                                     .collect(Collectors.toList());
        return suitableTrucks;
    }

    public void onTruckSelect(Truck truck) {

        showMessage("Truck changed, "+truck.toString(), true);
    }

    public void onSelectCheckboxChange(Order o) {

        logger.info("On checkbox change: "+o.getDrivers().size());
        o.getDrivers().stream().forEach(d -> logger.info(" : "+d.toString()));
        showMessage("onDriverSelect: drivers num = "+o.getDrivers().size(), true);
    }

    public List<Driver> getSuitableDrivers(Order order) {
        // toDO: implement ICountry and use it for time estimation
        int estimatedOrderTime = 8;

        return driverService.getVacantDrivers().stream()
                .filter(driver -> driver.getWorkingHours() < (driverService.getMaxWorkHours() - estimatedOrderTime))
                .collect(Collectors.toList());
    }

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
    public void addOrder() {
        List<Node> orderNodes = new ArrayList<>();
        cargoCycles.stream().forEach(cargoCycle -> cargoCycle.setHasCitiesError(false));
        boolean hasError = false;

        for (CargoCycle cc:cargoCycles) {
            // final check for different pickup/delivery points to handle usercase then user change city in already added node
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

        // check for max weight
        Collections.sort(orderNodes, new CityBasedComparator());
        newOrder.setNodes(orderNodes);
        newOrder.setMaxWeight(getOrderTotalWeight(newOrder));

        if (newOrder.getMaxWeight() > getTruckMaxCapacity()) {
            showMessage("Maximum load ("+newOrder.getMaxWeight()+" kg) exceed truck max payload ("+getTruckMaxCapacity()+" kg)");
            return;
        };

        orderService.saveOrder(newOrder);
        initNewOrder();
        loadData();
        showMessage("New order added", true);
    }

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
            // toDo: double check for cargo name not empty
        }

        CargoCycle currentCargoCycle = new CargoCycle();
        cargoCycles.add(currentCargoCycle);
    }

    public void removeCargo(CargoCycle cs) {
        cargoCycles.remove(cs);
    }

    public int getTruckMaxCapacity() {
       return truckService.getMaxCapacity();
    }

    public int getOrderTotalWeight(Order order) {

        if(order == null) return 0;
        List<Node> nodes = order.getNodes();
        if(nodes == null || nodes.isEmpty()) return 0;

        int weight = 0;
        int max = 0;

        for (int i=0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Cargo checkedCargo = node.getCargo();
            switch (node.getType()) {
                case LOAD:
                    weight += checkedCargo.getWeight();
                    break;
                case UNLOAD:
                    if (i == 0) break;
                    List<Node> previousNodes = nodes.subList(0, i);
                    boolean wasLoaded = previousNodes.stream().anyMatch(n -> n.getCargo().equals(checkedCargo));
                    if(wasLoaded) weight -= node.getCargo().getWeight();
                    break;
            }
            if (weight > max) max = weight;
        }
        return  max;
    }

    private void showMessage(String error, boolean... notError) {

        FacesMessage.Severity severity = (notError != null)&&(notError[0])? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, error, ""));
    }

}
