package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.Cargo;
import com.papatriz.jsfdemo.models.EActionType;
import com.papatriz.jsfdemo.models.Node;
import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.services.IOrderService;
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
        initNode.setCargo(testCargo);
        initNode.setType(EActionType.LOAD);

        newOrderNodes.add(initNode);

        newOrder.setComplete(false);
        newOrder.setNodes(newOrderNodes);
    }

    public void addOrder() {

        orderService.saveOrder(newOrder);
        newOrder = new Order();
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

    public void onCargoSelect(SelectEvent<String> event) {
        System.out.println("onCargoSelect :: "+event.getObject());

        String normalizedName = event.getObject().toLowerCase().trim();
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
