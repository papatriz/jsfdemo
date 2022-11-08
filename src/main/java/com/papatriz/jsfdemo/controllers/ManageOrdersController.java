package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.main.*;
import com.papatriz.jsfdemo.services.IOrderService;
import com.papatriz.jsfdemo.services.ITruckService;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Scope(value = "session")
@Component(value = "manageOrdersController")
@ELBeanName(value = "manageOrdersController")
@Join(path = "/manage_orders", to = "/manage_orders.xhtml")
public class ManageOrdersController {
    private final IOrderService orderService;
    private final ITruckService truckService;
    @Autowired
    public ManageOrdersController(IOrderService orderService, ITruckService truckService) {
        this.orderService = orderService;
        this.truckService = truckService;
    }

    private enum CargoCycleState {
        INIT,
        COMPLETE,
        ERROR
    }

    private List<Order> orders;
    private Order newOrder = new Order();
    private List<Node> newOrderNodes;
    private boolean validationFailed = false;

    @PostConstruct
    private void initNewOrder() {

        newOrderNodes = new ArrayList<>();
        Node initNode = new Node();
        validationFailed = false;

        Cargo testCargo = new Cargo();
        testCargo.setName("Bananas and vodka");
        testCargo.setWeight(500);
        testCargo.setStatus(ECargoStatus.PREPARED);

        initNode.setCargo(testCargo);
        initNode.setType(EActionType.LOAD);
        initNode.setFinalized(true);

        newOrderNodes.add(initNode);

        newOrder.setComplete(false);
        newOrder.setNodes(newOrderNodes);
        newOrder.getNodes().get(0).setOrder(newOrder);
    }

    public void addOrder() {

        Node lastNode = newOrderNodes.get(newOrderNodes.size()-1);
        String lastCargoName = lastNode.getCargo().getName();
        if(lastCargoName.isEmpty()) newOrderNodes.remove(lastNode);

        if (!finalCheck()) {
            showError("All cargos must have both load and unload points!");
            return;
        }

        orderService.saveOrder(newOrder);
        newOrder = new Order();
        initNewOrder();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New order added", ""));
    }

    private boolean finalCheck() {

        // check if all cargos has load and unload nodes
        List<Cargo> checkList = new ArrayList<>();
        for (Node node:newOrder.getNodes()) {
            if (node.getType() == EActionType.LOAD)
                checkList.add(node.getCargo());
            else
                checkList.remove(node.getCargo());
        }

        // check if total weight doesn't exceed max truck capacity

        return checkList.isEmpty();
    }

    public void addNode() {

        Node lastNode = newOrderNodes.get(newOrderNodes.size()-1);
        String lastCargoName = lastNode.getCargo().getName();

        if (lastCargoName.isEmpty()) {
            showError("Please, enter cargo name!");
            validationFailed = true;
            return;
        }

        CargoCycleState state = checkCargoCycleState(lastCargoName);

        if (state == CargoCycleState.COMPLETE) {
            Node previousCargoNode = newOrderNodes.stream().filter(s -> s.getCargo().getName().equals(lastCargoName)).findFirst().orElse(null);
            if (previousCargoNode.getCity() == lastNode.getCity()) {
                showError("Cargo has to be unloaded in different city!");
                validationFailed = true;
                return;
            }
        }

        if (state == CargoCycleState.ERROR) {
            showError("This cargo is already handled!");
            validationFailed = true;
            return;
        }

        if (truckService.getMaxCapacity() < getOrderMaxWeight(newOrder)) {
            showError("Total cargos weight exceed single truck max capacity!");
            validationFailed = true;
            return;
        }

        Node extraNode = new Node();
        extraNode.setType(EActionType.LOAD);
        extraNode.setFinalized(true);
        extraNode.setOrder(newOrder);

        newOrderNodes.add(extraNode);
        validationFailed = false;
    }

    public boolean isFinalized(Node checkedNode) {

        return  checkedNode.isFinalized();
    }

    public boolean hasErrorAndLast(Node checkedNode) {

        return (newOrderNodes.indexOf(checkedNode) == newOrderNodes.size()-1) && validationFailed;
    }

    public List<String> completeCargoName(String input) {

        String inputLowerCase = input.toLowerCase();
        List<String> cargoNames = newOrder.getNodes().stream().map(n -> n.getCargo().getName()).filter(Objects::nonNull).collect(Collectors.toList());

        return cargoNames.stream().filter(n -> n.toLowerCase().startsWith(inputLowerCase)).collect(Collectors.toList());
    }

    public void onCargoSelect(SelectEvent<String> event) {
        System.out.println("onCargoSelect :: "+ event.getObject());

        checkCargoCycleState(event.getObject());
     }

     private CargoCycleState checkCargoCycleState(String cargoName) {

        String normalizedName = cargoName.toLowerCase().trim();
        List<Node> nodes = newOrder.getNodes();
        Node lastNode = nodes.get(nodes.size()-1);

        for (int i = nodes.size()-2; i >= 0; i--) {

            if (nodes.get(i).getCargo().getName().toLowerCase().trim().equals(normalizedName) ) { // Found this cargo

                if(nodes.get(i).getType() == EActionType.UNLOAD) { // This cargo already fully handled
                  //  showError("This cargo already handled!");
                    return CargoCycleState.ERROR;
                }

                lastNode.setCargo(nodes.get(i).getCargo());
                lastNode.setType(EActionType.UNLOAD);
                lastNode.setFinalized(true);

                return CargoCycleState.COMPLETE;
             }
        }

        return CargoCycleState.INIT;
     }

    public List<Order> getGetPendingOrders() {

        return orderService.getPendingOrders();
    }

    private void showError(String error) {

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, error, ""));
    }

    public int getOrderMaxWeight(Order order) {
        int weight = 0;
        int max = 0;

        for (Node node: order.getNodes()) {
            switch (node.getType()) {
                case LOAD:
                    weight += node.getCargo().getWeight(); break;
                case UNLOAD:
                    weight -= node.getCargo().getWeight(); break;
            }
            if (weight > max) max = weight;
        }

        return  max;
    }
}
