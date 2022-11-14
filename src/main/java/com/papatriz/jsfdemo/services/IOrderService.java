package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.exceptions.NoLoadCargoPointException;
import com.papatriz.jsfdemo.models.main.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {

     void saveOrder(Order order);
     List<Order> getPendingOrders();
     List<Order> getActiveOrders();
     List<Order> getAllOrders();
     Page<Order> getLastNOrders(int n);
     void makeWayBill(Order order) throws NoLoadCargoPointException;
     int getOrderMaxWeight(Order order);

}
