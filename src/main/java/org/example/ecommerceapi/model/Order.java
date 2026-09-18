package org.example.ecommerceapi.model;

import jakarta.persistence.*;
import org.example.ecommerceapi.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal totalPrice;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    public Order(){}

    public Order(BigDecimal totalPrice, OrderStatus status, LocalDateTime createdAt, User user){
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addItem(OrderItem item){
        items.add(item);
        item.setOrder(this);
    }
}
