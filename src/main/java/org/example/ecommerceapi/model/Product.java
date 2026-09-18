package org.example.ecommerceapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private BigDecimal price;

    @Column
    private int inventory;

    public Product(){}
    public Product(String name, String description, BigDecimal price, int inventory){
        this.name = name;
        this.description = description;
        this.price = price;
        this.inventory = inventory;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getInventory() {
        return inventory;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
