package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITruckRepository extends JpaRepository<Truck, String> {

    @Query(value = "SELECT max(capacity) FROM Truck ")
    int getMaxCapacity();

}
