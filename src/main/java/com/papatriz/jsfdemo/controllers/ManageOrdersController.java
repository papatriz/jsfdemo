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

    @PostConstruct
    private void initNewOrder() {

        newOrderNodes = new ArrayList<>();
        Node initNode = new Node();

        Cargo testCargo = new Cargo();
        testCargo.setName("Test cargo");
        testCargo.setWeight(500);
        testCargo.setStatus(ECargoStatus.PREPARED);

        initNode.setCargo(testCargo);
        initNode.setType(EActionType.LOAD);
      //  initNode.setOrder(newOrder);

        newOrderNodes.add(initNode);

        newOrder.setComplete(false);
        newOrder.setNodes(newOrderNodes);
        newOrder.getNodes().get(0).setOrder(newOrder);
    }

    public void addOrder() {

        orderService.saveOrder(newOrder);
        newOrder = new Order();
        initNewOrder();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New order added", ""));
    }

    public void addNode() {
        Node extraNode = new Node();
        extraNode.setOrder(newOrder);
        newOrderNodes.add(extraNode);
    }

    public boolean isFirstOrLastNode(Node checkedNode) {

        return newOrderNodes.indexOf(checkedNode) == 0;
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
    public void onCargoSelect(SelectEvent event) {
        System.out.println("onCargoSelect :: "+(String)event.getObject());

        String normalizedName = (String)(event.getObject());
        List<Node> nodes = newOrder.getNodes();

        for (int i = 0; i < nodes.size()-1; i++) {
            System.out.println("Cargos in this order :: "+nodes.get(i).getCargo().getName() + " w: " + nodes.get(i).getCargo().getWeight());
            if (nodes.get(i).getCargo().getName().toLowerCase().trim().equals(normalizedName) ) {
                System.out.println("Find similar!");
                nodes.get(nodes.size()-1).setCargo(nodes.get(i).getCargo());
            }
        }
     }
}
