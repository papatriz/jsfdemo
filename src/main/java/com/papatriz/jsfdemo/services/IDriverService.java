package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.main.Driver;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDriverService {
    int getMaxWorkHours();
    List<Driver> getAllDrivers();
    List<Driver> getVacantDrivers();
    Optional<Driver> getByUserId(UUID uuid);
    Page<Driver> getDriversPageable(int page, int size, boolean sorted);
    Driver getDriverById(int id);
    void saveDriver(Driver driver);
    void removeDriver(Driver driver);
    void removeDriverById(int id);
    void updateDriver(Driver driver);
}
