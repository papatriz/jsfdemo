package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.services.IOrderService;
import com.papatriz.jsfdemo.services.ITruckService;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.event.ItemSelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Scope(value = "session")
@Component(value = "ordersController")
@ELBeanName(value = "ordersController")
@Join(path = "/manage_orders_new", to = "/manage_orders_new.xhtml")
public class ManageOrdersControllerNew {

    private final IOrderService orderService;
    private final ITruckService truckService;
    @Autowired
    public ManageOrdersControllerNew(IOrderService orderService, ITruckService truckService) {
        this.orderService = orderService;
        this.truckService = truckService;
    }

    private Order newOrder = new Order();
    private List<CargoCycle> cargoCycles;

    class CityBasedComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) { // toDo: after implementation of ICountry make sort on distance base
            if (o1.getCity().equals(o2.getCity())) return 0;
            return o1.getCity().ordinal() > o2.getCity().ordinal() ? 1 : -1;
        }
    }

    @PostConstruct
    private void init() {
        cargoCycles = new ArrayList<>();
        CargoCycle currentCargoCycle = new CargoCycle();
        cargoCycles.add(currentCargoCycle);

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

            orderNodes.add(cc.getLoadNode());
            orderNodes.add(cc.getUnloadNode());
        }

        if (hasError) {
            showError("Delivery has to be in different city");
            return;
        }

        // check for max weight
        Collections.sort(orderNodes, new CityBasedComparator());

        if (getOrderTotalWeight(orderNodes) > getTruckMaxCapacity()) {
            showError("Maximum load ("+getOrderTotalWeight(orderNodes)+" kg) exceed truck max payload ("+getTruckMaxCapacity()+" kg)");
            return;
        };
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

    public void onItemSelected(ItemSelectEvent event) {
        System.out.println(event.getClass());

    }
    public void onValueChange(ValueChangeEvent event) {

        System.out.println(event.getNewValue().getClass());
    }
    public int getTruckMaxCapacity() {
       return truckService.getMaxCapacity();
    }

    private int getOrderTotalWeight(List<Node> nodes) {
        int weight = 0;
        int max = 0;

        for (int i=0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            System.out.println(" > "+node.getCity()+" :: "+node.getType() +" : "+node.getCargo().getWeight());
            Cargo checkedCargo = node.getCargo();
            switch (node.getType()) {
                case LOAD:
                    weight += checkedCargo.getWeight();
                    break;
                case UNLOAD:
                    List<Node> previousNodes = nodes.subList(0, i-1);
                    boolean wasLoaded = !previousNodes.stream().filter(n -> n.getCargo().equals(checkedCargo)).collect(Collectors.toList()).isEmpty();
                    if(wasLoaded) weight -= node.getCargo().getWeight();
                    break;
            }
            if (weight > max) max = weight;
        }
        System.out.println("Max weight at moment: "+ max);
        return  max;
    }

    private void showError(String error, boolean... notError) {

        FacesMessage.Severity severity = notError == null? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_ERROR;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, error, ""));
    }
}
