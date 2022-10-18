package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.events.TruckTableChangedEvent;
import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.services.IDriverService;
import com.papatriz.jsfdemo.services.IOrderService;
import com.papatriz.jsfdemo.services.ITruckService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
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
    private List<CargoCycle> cargoCycles;
    private boolean needUpdate;
    private Map<Integer, List<Driver>> testDriversMap = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(ManageOrdersControllerNew.class);



    class CityBasedComparator implements Comparator<Node> {
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

      //  orderService.getAllOrders();
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
        List<Truck> suitableTrucks = cachedTrucks.stream().filter(truck -> truck.isAvailable() && (truck.getCapacity() >= maxWeight))
                                        .collect(Collectors.toList());
        return suitableTrucks;
    }

    public void onTruckSelect(Truck truck) {


        showError("Truck changed, "+truck.toString());
    }

    public void onSelectCheckboxChange(Order o) {

        logger.info("On checkbox change: "+o.getDrivers().size());
        o.getDrivers().stream().forEach(d -> logger.info(" : "+d.toString()));
        showError("onDriverSelect: drivers num = "+o.getDrivers().size());
    }

    public List<Driver> getSuitableDrivers(Order order) {

        return driverService.getAllDrivers();
    }

    public void updateOrder(Order order) {
        order.getAssignedTruck().setOrder(order);
        order.getAssignedTruck().setAssignedDrivers(order.getDrivers());
        truckService.saveTruck(order.getAssignedTruck());
        order.getDrivers().stream().forEach(driver -> {driver.setOrder(order); driver.setCurrentTruck(order.getAssignedTruck()); driverService.saveDriver(driver);});

        orderService.saveOrder(order);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order saved and active now", ""));

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
            showError("Delivery has to be in different city");
            return;
        }

        // check for max weight
        Collections.sort(orderNodes, new CityBasedComparator());
        newOrder.setNodes(orderNodes);
        newOrder.setMaxWeight(getOrderTotalWeight(newOrder));

        if (newOrder.getMaxWeight() > getTruckMaxCapacity()) {
            showError("Maximum load ("+getOrderTotalWeight(newOrder)+" kg) exceed truck max payload ("+getTruckMaxCapacity()+" kg)");
            return;
        };

        orderService.saveOrder(newOrder);
        initNewOrder();
        loadData();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New order added", ""));

    }

    public void addCargo() {

        if (!cargoCycles.isEmpty()) {

            CargoCycle lastCC = cargoCycles.get(cargoCycles.size() - 1);
            lastCC.setHasWeightError(false);
            lastCC.setHasCitiesError(false);

            // check for different load/unload points
            if (lastCC.getLoadNode().getCity() == lastCC.getUnloadNode().getCity()) {
                showError("Delivery has to be in different city");
                lastCC.setHasCitiesError(true);
                return;
            }
            // check for single cargo max weight
            if (lastCC.getCargo().getWeight() > getTruckMaxCapacity()) {
                showError("Cargo weight exceed max truck capacity");
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

        List<Node> nodes = order.getNodes();

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
                    boolean wasLoaded = !previousNodes.stream().filter(n -> n.getCargo().equals(checkedCargo)).collect(Collectors.toList()).isEmpty();
                    if(wasLoaded) weight -= node.getCargo().getWeight();
                    break;
            }
            if (weight > max) max = weight;
            System.out.println(" > "+node.getCity()+" :: "+node.getType() +" : "+node.getCargo().getWeight()+"  In truck: "+weight);

        }
        System.out.println("Max weight at moment: "+ max);
        return  max;
    }

    private void showError(String error, boolean... notError) {

        FacesMessage.Severity severity = notError == null? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, error, ""));
    }


}
