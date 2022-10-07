package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
@Query("select o from Order o where o.assignedTruck is null")
List<Order> getPendingOrders();

    @Query(value = "select *  from orders inner join truck t on orders.id != t.order_id", nativeQuery = true)
    List<Order> getPendingOrders2();
}
