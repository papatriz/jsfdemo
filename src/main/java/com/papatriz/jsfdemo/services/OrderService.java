package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Node;
import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.repositories.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService implements IOrderService{
    private final IOrderRepository orderRepository;
    @Autowired
    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> getPendingOrders() {

        List<Order> orders = orderRepository.getPendingOrders2();
        List<Node> nodes = new ArrayList<>();
        orders.stream().forEach(o -> nodes.addAll(o.getNodes()));
        nodes.stream().forEach(n -> n.getCargo().getWeight());
        return orders;
    }

    @Override
    public List<Order> getActiveOrders() {

        return orderRepository.getActiveOrdersHQL();
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<Node> nodes = new ArrayList<>();
        orders.stream().forEach(o -> nodes.addAll(o.getNodes()));
        nodes.stream().forEach(n -> System.out.println(n.getCargo().getWeight()));
        return orders;
    }
}
