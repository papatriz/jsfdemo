package com.papatriz.jsfdemo.repositories.main;

import com.papatriz.jsfdemo.models.main.Driver;
import com.papatriz.jsfdemo.models.main.EDriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {
    @Modifying
    @Query("delete from Driver d where d.id = ?1")
    void deleteCustom(int entityId);

    List<Driver> findByStatus(EDriverStatus status);
    Optional<Driver> findDriverByUserId(UUID uuid);
}