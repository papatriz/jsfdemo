package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Driver;
import com.papatriz.jsfdemo.models.EDriverStatus;
import com.papatriz.jsfdemo.repositories.IDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DriverService implements IDriverService {

    private final int MAX_WORK_HOURS = 176;
    private final IDriverRepository driverRepository;

    @Autowired
    public DriverService(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public int getMaxWorkHours() {
        return MAX_WORK_HOURS;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public List<Driver> getVacantDrivers() {
        return driverRepository.findByStatus(EDriverStatus.READY);
    }

    @Override
    public Page<Driver> getDriversPageable(int page, int size, boolean sorted) {
        return null;
    }

    @Override
    @Transactional
    public Driver getDriverById(int id) {
        Driver d = driverRepository.findById(id).orElse(null);
        return d;
    }

    @Override
    @Transactional
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void removeDriver(Driver driver) {
        driverRepository.delete(driver);
    }
    @Override
    @Transactional
    public void removeDriverById(int id) {
        driverRepository.deleteCustom(id);
    }



    @Override
    public void updateDriver(Driver driver) {

    }
}
