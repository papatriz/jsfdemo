package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
}
