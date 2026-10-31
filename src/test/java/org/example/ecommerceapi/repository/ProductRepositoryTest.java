package org.example.ecommerceapi.repository;

import org.example.ecommerceapi.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;


    @Autowired
    private ProductRepository productRepository;


    @Test
    void findProductByNameContainingIgnoreCase_ReturnsMatchingProducts(){
        Product iPhoneProductToSave = new Product("Apple Iphone 15", "Smartphone (apple)", BigDecimal.valueOf(650), 10);
        Product macBookProductToSave = new Product("MacBook Pro (apple)", "Laptop", BigDecimal.valueOf(2400), 3);
        Product samsungProductToSave = new Product("Samsung Galaxy S24", "Smartphone (samsung)", BigDecimal.valueOf(700), 10);

        productRepository.save(iPhoneProductToSave);
        productRepository.save(macBookProductToSave);
        productRepository.save(samsungProductToSave);

        entityManager.flush();
        entityManager.clear();

        List<Product> products = productRepository.findProductByNameContainingIgnoreCase("APPLE");

        assertThat(products).extracting(Product::getName).containsExactlyInAnyOrder("Apple Iphone 15", "MacBook Pro (apple)");


    }

    @Test
    void findByIdWithLock_ReturnsProduct(){
        Product saved = productRepository.save(new Product("Apple Iphone 15", "Smartphone (apple)", BigDecimal.valueOf(650), 10));


        entityManager.flush();
        entityManager.clear();

        Optional<Product> result = productRepository.findByIdWithLock(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(saved.getName());

    }
}
