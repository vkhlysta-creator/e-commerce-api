package org.example.ecommerceapi.repository;

import org.example.ecommerceapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByEmail(String email);
}
