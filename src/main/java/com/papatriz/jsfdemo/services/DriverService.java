package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Driver;
import com.papatriz.jsfdemo.repositories.IDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService implements IDriverService {

    private final IDriverRepository driverRepository;

    @Autowired
    public DriverService(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public Page<Driver> getDriversPageable(int page, int size, boolean sorted) {
        return null;
    }

    @Override
    public Driver getDriverById(int id) {
        return null;
    }

    @Override
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    @Override
    public void removeDriver(int id) {

    }

    @Override
    public void updateDriver(Driver driver) {

    }
}
