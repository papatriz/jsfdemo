package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
@Query("from Order as ord where ord.assignedTruck is not null and ord.drivers.size > 0")
List<Order> getActiveOrdersHQL();

    @Query(value = "select *  from orders o where not exists( select * from truck where truck.order_id = o.id )", nativeQuery = true)
    List<Order> getPendingOrders2();

   // List<Order> getActiveOrders();
}
