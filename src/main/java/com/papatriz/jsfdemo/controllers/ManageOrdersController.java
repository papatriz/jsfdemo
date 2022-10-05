package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.services.IOrderService;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Scope(value = "session")
@Component(value = "manageOrdersController")
@ELBeanName(value = "manageOrdersController")
@Join(path = "/manage_orders", to = "/manage_orders.xhtml")
public class ManageOrdersController {
    private final IOrderService orderService;
    @Autowired
    public ManageOrdersController(IOrderService orderService) {
        this.orderService = orderService;
    }

    private List<Order> orders;
    private Order newOrder = new Order();
    private List<Node> newOrderNodes;
    private boolean validationFailed = false;

    @PostConstruct
    private void initNewOrder() {

        newOrderNodes = new ArrayList<>();
        Node initNode = new Node();

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

        orderService.saveOrder(newOrder);
        newOrder = new Order();
        initNewOrder();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New order added", ""));
    }

    public void addNode() {

        Node lastNode = newOrderNodes.get(newOrderNodes.size()-1);
        String lastCargoName = lastNode.getCargo().getName();
        if (lastCargoName.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please, enter cargo name!", ""));
            validationFailed = true;
            return;
        }
        if (checkSameCargo(lastCargoName)) {
            Node previosCargoNode = newOrderNodes.stream().filter(s -> s.getCargo().getName().equals(lastCargoName)).findFirst().orElse(null);
            if (previosCargoNode.getCity() == lastNode.getCity()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cargo has to be unloaded in different city!", ""));
                validationFailed = true;
                return;
            }
        }

        Node extraNode = new Node();
        extraNode.setType(EActionType.LOAD);
        extraNode.setFinalized(true);
        extraNode.setOrder(newOrder);
        newOrderNodes.add(extraNode);
        validationFailed = false;
    }

    public boolean isFirstOrLastNode(Node checkedNode) {

        return checkedNode.isFinalized();
    }

    public List<String> completeCargoName(String input) {

        String inputLowerCase = input.toLowerCase();
        List<String> cargoNames = newOrder.getNodes().stream().map(n -> n.getCargo().getName()).filter(s -> s!=null).collect(Collectors.toList());

        return cargoNames.stream().filter(n -> n.toLowerCase().startsWith(inputLowerCase)).collect(Collectors.toList());
    }

    public void onSelectTest(AjaxBehaviorEvent theEvent) {
        AutoComplete anAutoComplete = (AutoComplete) theEvent.getComponent();
        String aSelection = anAutoComplete.getValue().toString();

        System.out.println("onSelectTest :: "+aSelection);


    }
    public void onCargoSelect(SelectEvent<String> event) {
        System.out.println("onCargoSelect :: "+ event.getObject());

        checkSameCargo(event.getObject());
     }

     private boolean checkSameCargo(String cargoName) {

        String normalizedName = cargoName.toLowerCase().trim();
        List<Node> nodes = newOrder.getNodes();
        Node lastNode = nodes.get(nodes.size()-1);

        for (int i = 0; i < nodes.size()-1; i++) {
            if (nodes.get(i).getCargo().getName().toLowerCase().trim().equals(normalizedName) ) {
                 lastNode.setCargo(nodes.get(i).getCargo());
                 lastNode.setType(EActionType.UNLOAD);
                 lastNode.setFinalized(true);

                return true;
             }
        }

        return false;
     }
}
