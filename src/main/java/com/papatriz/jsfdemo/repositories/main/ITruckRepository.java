package com.papatriz.jsfdemo.repositories.main;

import com.papatriz.jsfdemo.models.main.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ITruckRepository extends JpaRepository<Truck, String> {

    @Query(value = "SELECT max(capacity) FROM Truck ")
    Optional<Integer> getMaxCapacity();

}
