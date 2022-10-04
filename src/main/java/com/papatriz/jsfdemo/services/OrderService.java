package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.repositories.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return null;
    }

    @Override
    public List<Order> getActiveOrders() {
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        return null;
    }
}
