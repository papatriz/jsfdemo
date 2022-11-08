package com.papatriz.jsfdemo.models.main;


import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "orders")
public class Order {
    // ----- SELF DATA -----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "complete")
    private boolean isComplete;

    // ----- RELATED DATA -----
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy
    private List<Node> nodes;

    @OneToOne(mappedBy = "order")
    private Truck assignedTruck;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Driver> drivers;

    @Transient
    private int maxWeight;

    public String getStatus() {
        if (assignedTruck==null) return "No truck";
        if (drivers.isEmpty()) return "No drivers";
        boolean waitDrivers = drivers.stream().anyMatch(d -> d.getStatus() == EDriverStatus.ASSIGNED || d.getStatus() == EDriverStatus.READY);
        if (waitDrivers) return "Waiting for drivers";
        if (isComplete) return "Completed";
        return "In progress";
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", isComplete=" + isComplete +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && isComplete == order.isComplete && Objects.equals(nodes, order.nodes) && Objects.equals(assignedTruck, order.assignedTruck) && Objects.equals(drivers, order.drivers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isComplete, assignedTruck);
    }
}
