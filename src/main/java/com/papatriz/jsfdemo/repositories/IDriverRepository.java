package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {
}