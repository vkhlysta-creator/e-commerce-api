package org.example.ecommerceapi.repository;

import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query("SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user = :user")
    List<CartItem> getCartItemsByUser(User user);

    @Modifying
    @Query("SELECT c FROM CartItem c JOIN FETCH c.product WHERE c.user = :user AND c.product = :product")
    Optional<CartItem> getCartItemByUserAndProduct(User user, Product product);
}
