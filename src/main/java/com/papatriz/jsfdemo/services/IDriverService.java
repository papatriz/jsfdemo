package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.models.Driver;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDriverService {
    List<Driver> getAllDrivers();
    Page<Driver> getDriversPageable(int page, int size, boolean sorted);
    Driver getDriverById(int id);
    void saveDriver(Driver driver);
    void removeDriver(int id);
    void updateDriver(Driver driver);
}
