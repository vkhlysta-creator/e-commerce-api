package org.example.ecommerceapi.config;

import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile({"dev", "local"})
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User("admin@example.com", passwordEncoder.encode("admin123"), Role.ADMIN);
            User user = new User("test@example.com", passwordEncoder.encode("password123"), Role.USER);

            userRepository.save(admin);
            userRepository.save(user);
            System.out.println("✅ Users seeded!");
        }

        if (productRepository.count() == 0) {
            Product p1 = new Product("iPhone 15 Pro", "Apple Smartphone 256GB", new BigDecimal("1099.99"), 50);
            Product p2 = new Product("MacBook Air M2", "Apple Laptop 512GB", new BigDecimal("1299.50"), 30);
            Product p3 = new Product("AirPods Pro 2", "Wireless Noise-Cancelling Earbuds", new BigDecimal("249.00"), 100);

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);
            System.out.println("✅ Products seeded!");
        }
    }
}
