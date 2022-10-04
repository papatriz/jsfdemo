package com.papatriz.jsfdemo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private ECity city;
    @Enumerated(EnumType.STRING)
    private EActionType type;

    @OneToOne()
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private Cargo cargo;

    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public Node() {
        this.cargo = new Cargo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id == node.id && city == node.city && type == node.type && Objects.equals(cargo, node.cargo) && Objects.equals(order, node.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, type, cargo, order);
    }

    public Node(Cargo cargo) {
        this.cargo = cargo;
        cargo.setCurrentNode(this);
    }
}
