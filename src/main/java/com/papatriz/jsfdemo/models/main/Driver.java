package com.papatriz.jsfdemo.models.main;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

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

    @Column(name = "updated")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date statusUpdateTime;

    @Column(name = "userid")
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID userId;

    @Transient
    private String email;
    @Transient
    private final int MILLIS_TO_HOURS = 3600000;

    public int getWorkingHours() {
        int workHours = 0;
        Calendar currCalendar = new GregorianCalendar();
        Calendar lastCalendar = new GregorianCalendar();
        Date tmp = (statusUpdateTime == null)? new Date() : statusUpdateTime;
        lastCalendar.setTime(tmp);

        int currMonth = currCalendar.get(Calendar.MONTH);
        int lastUpdateMonth = lastCalendar.get(Calendar.MONTH);
        int hoursFromLastUpdate = (int) ((currCalendar.getTimeInMillis() - lastCalendar.getTimeInMillis()) / MILLIS_TO_HOURS);

        workHours = hoursWorked;
        if (currMonth > lastUpdateMonth) {
            workHours = 0;
            if (status == EDriverStatus.DRIVE) {
                Calendar startMonth =  new GregorianCalendar();
                startMonth.set(currCalendar.get(Calendar.YEAR), currMonth, 0,0,0,0);
                hoursFromLastUpdate = (int) ((currCalendar.getTimeInMillis() - startMonth.getTimeInMillis()) / MILLIS_TO_HOURS);
            }
        }

        if (status == EDriverStatus.DRIVE) workHours += hoursFromLastUpdate;

        return workHours;
    }

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
