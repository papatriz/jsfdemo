package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.exceptions.NoLoadCargoPointException;
import com.papatriz.jsfdemo.models.*;
import com.papatriz.jsfdemo.services.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ManageOrdersControllerNewTest {

    @Autowired
    IOrderService orderService;

    @Test
    void getOrderTotalWeight_SimpleOrder() {
        assertEquals(100, orderService.getOrderMaxWeight(createSimpleTestOrder()));
    }
    @Test
    void getOrderTotalWeight_ComplexOrder() {
        assertEquals(150, orderService.getOrderMaxWeight(createComplexTestOrder()));
    }
    @Test
    void getOrderTotalWeight_NullOrder() {
        assertEquals(0, orderService.getOrderMaxWeight(null));
    }
    @Test
    void getOrderTotalWeight_NewOrder() {
        assertEquals(0, orderService.getOrderMaxWeight(new Order()));
    }

    // Data creation
    Order createSimpleTestOrder() {
        Cargo cargo01 = new Cargo();
        cargo01.setName("Cargo1");
        cargo01.setWeight(100);

        Node node01 = new Node();
        node01.setCity(ECity.Moscow);
        node01.setCargo(cargo01);
        node01.setType(EActionType.LOAD);

        Node node02 = new Node();
        node02.setCity(ECity.Petersburg);
        node02.setCargo(cargo01);
        node02.setType(EActionType.UNLOAD);

        Order order = new Order();
        List<Node> nodes= new ArrayList<>();
        nodes.add(node01);
        nodes.add(node02);
        order.setNodes(nodes);

        return order;
    }

    Order createComplexTestOrder() {
        Cargo cargo01 = new Cargo();
        cargo01.setName("Cargo1");
        cargo01.setWeight(100);

        Cargo cargo02 = new Cargo();
        cargo02.setName("Cargo2");
        cargo02.setWeight(150);

        Node node01 = new Node();
        node01.setCity(ECity.Moscow);
        node01.setCargo(cargo01);
        node01.setType(EActionType.LOAD);

        Node node02 = new Node();
        node02.setCity(ECity.Petersburg);
        node02.setCargo(cargo01);
        node02.setType(EActionType.UNLOAD);

        Node node03 = new Node();
        node03.setCity(ECity.Petersburg);
        node03.setCargo(cargo02);
        node03.setType(EActionType.LOAD);

        Node node00 = new Node();
        node00.setCity(ECity.Moscow);
        node00.setCargo(cargo02);
        node00.setType(EActionType.UNLOAD);

        Order order = new Order();
        List<Node> nodes= new ArrayList<>();
        nodes.add(node00);
        nodes.add(node01);
        nodes.add(node02);
        nodes.add(node03);
        order.setNodes(nodes);
        try {
            orderService.makeWayBill(order);
        } catch (NoLoadCargoPointException e) {
            throw new RuntimeException(e);
        }

        return order;
    }
}