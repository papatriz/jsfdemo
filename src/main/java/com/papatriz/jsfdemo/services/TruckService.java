package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.events.TruckTableChangedEvent;
import com.papatriz.jsfdemo.models.Order;
import com.papatriz.jsfdemo.models.Truck;
import com.papatriz.jsfdemo.repositories.ITruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TruckService implements ITruckService{

    private final ITruckRepository truckRepository;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public TruckService(ITruckRepository truckRepository, ApplicationEventPublisher publisher) {
        this.truckRepository = truckRepository;
        this.publisher = publisher;
    }
    @Override
    public List<Truck> getAllTrucks() {

        return truckRepository.findAll();
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
    @Transactional
    public void saveTruck(Truck truck) {

        truckRepository.save(truck);
        publisher.publishEvent(new TruckTableChangedEvent("add"));
    }

    @Override
    @Transactional
    public void removeTruck(Truck truck) {
        truckRepository.delete(truck);
        publisher.publishEvent(new TruckTableChangedEvent("delete"));

    }

    @Override
    public int getMaxCapacity() {
        int max = truckRepository.getMaxCapacity().orElse(0);
        return max;
    }

}
