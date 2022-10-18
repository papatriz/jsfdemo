package com.papatriz.jsfdemo.models;


import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
public class  Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;
    @Column
    private String surname;

    @Column(name = "hours")
    private int hoursWorked;

    @Enumerated(EnumType.STRING)
    private EDriverStatus status;

    @Column(name = "city")
    private String currentCity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && name.equals(driver.name) && surname.equals(driver.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }

    @ManyToOne()
    @JoinColumn(name="truck", referencedColumnName = "regnum")
    private Truck currentTruck;

    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Override
    public String toString() {
        return  name + " " + surname+", id: " + id;
    }
}
