package com.adobe.bookstore.status;

import com.adobe.bookstore.order.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private OrderStatus  name;

    @JsonIgnore
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Order> orders;

    public Long getId() {
        return id;
    }

    public OrderStatus getName() {
        return name;
    }

    public void setName(OrderStatus name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}