package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITruckRepository extends JpaRepository<Truck, String> {
}
