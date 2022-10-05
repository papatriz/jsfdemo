package com.papatriz.jsfdemo.models;


import lombok.Data;

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
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST})
    private List<Node> nodes;

    @OneToOne(mappedBy = "order")
    private Truck assignedTruck;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST})
    private List<Driver> drivers;

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
