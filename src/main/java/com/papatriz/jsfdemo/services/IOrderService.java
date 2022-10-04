package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Order;

import java.util.List;

public interface IOrderService {

     void saveOrder(Order order);
     List<Order> getPendingOrders();
     List<Order> getActiveOrders();
     List<Order> getAllOrders();

}
