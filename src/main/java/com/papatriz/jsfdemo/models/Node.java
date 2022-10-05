package com.papatriz.jsfdemo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "nodes")
@Data
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private ECity city;
    @Enumerated(EnumType.STRING)
    private EActionType type;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private Cargo cargo;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public Node() {
        this.cargo = new Cargo();
        this.cargo.setCurrentNode(this);
        this.cargo.setStatus(ECargoStatus.PREPARED);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", city=" + city +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && city == node.city && type == node.type && Objects.equals(cargo, node.cargo) && Objects.equals(order, node.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, type);
    }
}
