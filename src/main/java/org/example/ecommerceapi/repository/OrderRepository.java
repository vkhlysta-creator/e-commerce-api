package org.example.ecommerceapi.repository;

import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);

}
