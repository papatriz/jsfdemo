package com.papatriz.jsfdemo.repositories.main;

import com.papatriz.jsfdemo.models.main.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICargoRepository extends JpaRepository<Cargo, Integer> {
}
