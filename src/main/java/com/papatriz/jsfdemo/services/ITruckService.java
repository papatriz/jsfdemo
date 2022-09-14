package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.models.Truck;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
public interface ITruckService {
    List<Truck> getAllTrucks();
    Page<Truck> getTrucksPageable(int page, int size, boolean sorted);
    List<Truck> getSuitableTrucks(Order order);
    Truck getTruckById(String regnum);
    void saveTruck(Truck truck);
    void removeTruck(String regNum);
    void updateTruck(Truck truck);
}
