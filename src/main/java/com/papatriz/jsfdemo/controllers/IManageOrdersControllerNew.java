package com.papatriz.jsfdemo.controllers;

import com.papatriz.jsfdemo.events.TruckTableChangedEvent;
import com.papatriz.jsfdemo.exceptions.NoLoadCargoPointException;
import com.papatriz.jsfdemo.models.main.CargoCycle;
import com.papatriz.jsfdemo.models.main.Driver;
import com.papatriz.jsfdemo.models.main.Order;
import com.papatriz.jsfdemo.models.main.Truck;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IManageOrdersControllerNew {

    @EventListener
    void afterFleetChanged(TruckTableChangedEvent event);

    List<Truck> getSuitableTrucks(Order order);

    void onTruckSelect(Truck truck);

    void onSelectCheckboxChange(Order o);

    List<Driver> getSuitableDrivers(Order order);

    void updateOrder(Order order);

    void addOrder() throws NoLoadCargoPointException;

    void addCargo();

    void removeCargo(CargoCycle cs);

    int getTruckMaxCapacity();

    com.papatriz.jsfdemo.services.IOrderService getOrderService();

    com.papatriz.jsfdemo.services.ITruckService getTruckService();

    com.papatriz.jsfdemo.services.IDriverService getDriverService();

    Order getNewOrder();

    List<Truck> getCachedTrucks();

    List<Order> getCachedPendingOrders();

    List<Order> getCachedActiveOrders();

    List<CargoCycle> getCargoCycles();

    void setNewOrder(Order newOrder);

    void setCachedTrucks(List<Truck> cachedTrucks);

    void setCachedPendingOrders(List<Order> cachedPendingOrders);

    void setCachedActiveOrders(List<Order> cachedActiveOrders);

    void setCargoCycles(List<CargoCycle> cargoCycles);
}
