package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.models.Truck;
import com.papatriz.jsfdemo.repositories.ITruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckService implements ITruckService{

    private final ITruckRepository truckRepository;

    @Autowired
    public TruckService(ITruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }
    @Override
    public List<Truck> getAllTrucks() {

        return truckRepository.findAll();
    }

    @Override
    public Page<Truck> getTrucksPageable(int page, int size, boolean sorted) {
        return null;
    }

    @Override
    public List<Truck> getSuitableTrucks(Order order) {
        return null;
    }

    @Override
    public Truck getTruckById(String regnum) {
        return null;
    }

    @Override
    public void saveTruck(Truck truck) {

    }

    @Override
    public void removeTruck(String regNum) {

    }

    @Override
    public void updateTruck(Truck truck) {

    }
}
