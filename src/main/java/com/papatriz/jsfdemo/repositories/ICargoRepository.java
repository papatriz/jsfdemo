package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICargoRepository extends JpaRepository<Cargo, Integer> {
}
