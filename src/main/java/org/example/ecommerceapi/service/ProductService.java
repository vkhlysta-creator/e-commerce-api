package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.ProductRequest;
import org.example.ecommerceapi.dto.ProductResponse;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = repository.findAll();

        return products.stream()
                .map(
                        product -> new ProductResponse(
                                product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getInventory()
                        )
                )
                .toList();

    }

    public ProductResponse createNewProduct(ProductRequest request) {
        Product createdProduct = repository.save(
                new Product(request.name(), request.description(), request.price(), request.inventory())
        );

        return new ProductResponse(
                createdProduct.getId(), createdProduct.getName(), createdProduct.getDescription(), createdProduct.getPrice(), createdProduct.getInventory()
        );
    }

    public List<ProductResponse> findByNameContaining(String name) {
        List<Product> products = repository.findProductByNameContainingIgnoreCase(name);

        return products.stream()
                .map(product -> new ProductResponse(
                                product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getInventory()
                        )
                )
                .toList();

    }


}
