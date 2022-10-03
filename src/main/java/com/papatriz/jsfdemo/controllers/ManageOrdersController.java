package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.models.Cargo;
import com.papatriz.jsfdemo.models.Node;
import com.papatriz.jsfdemo.models.Order;
import lombok.Data;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        newOrderNodes.add(initNode);
        newOrderNodes.add(initNode);


        newOrder.setComplete(false);
        newOrder.setNodes(newOrderNodes);
    }

    public void addNode() {
        newOrderNodes.add(new Node());
    }


}
