package org.example.ecommerceapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public CartItem(){}
    public CartItem(int quantity, User user, Product product){
        this.quantity = quantity;
        this.user = user;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
