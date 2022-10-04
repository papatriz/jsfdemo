package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.Cargo;
import com.papatriz.jsfdemo.models.EActionType;
import com.papatriz.jsfdemo.models.Node;
import com.papatriz.jsfdemo.models.Order;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Scope(value = "session")
@Component(value = "manageOrdersController")
@ELBeanName(value = "manageOrdersController")
@Join(path = "/manage_orders", to = "/manage_orders.xhtml")
public class ManageOrdersController {

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

    public void addNode() {

        newOrderNodes.add(new Node());
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

        Cargo tmpCargo;
        String normalizedName = event.getObject().toLowerCase().trim();
        List<Node> nodes = newOrder.getNodes();
      //  nodes.remove(nodes.size()-1);

        for (int i = 0; i < nodes.size()-1; i++) {
            System.out.println("Cargos in this order :: "+nodes.get(i).getCargo().getName() + " w: " + nodes.get(i).getCargo().getWeight());
            if (nodes.get(i).getCargo().getName().toLowerCase().trim().equals(normalizedName) ) {
                System.out.println("Find similar!");
                nodes.get(nodes.size()-1).setCargo(nodes.get(i).getCargo());
            }
        }
     }
}
