package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.models.Truck;
import com.papatriz.jsfdemo.repositories.ITruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<Truck> getTruckById(String regnum) {
        return truckRepository.findById(regnum);
    }

    @Override
    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }

    @Override
    public void removeTruck(Truck truck) {
        truckRepository.delete(truck);
    }

    @Override
    public void updateTruck(Truck truck) {

    }
}
