package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.ProductRequest;
import org.example.ecommerceapi.dto.ProductResponse;
import org.example.ecommerceapi.exception.ProductNotFoundException;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ProductResponse> findByNameContaining(String name) {
        List<Product> products = repository.findProductByNameContainingIgnoreCase(name);

        return products.stream()
                .map(product -> new ProductResponse(
                                product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getInventory()
                        )
                )
                .toList();

    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product fetchedProduct = repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));

        return new ProductResponse(
                fetchedProduct.getId(), fetchedProduct.getName(), fetchedProduct.getDescription(), fetchedProduct.getPrice(), fetchedProduct.getInventory()
        );
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product fetchedProduct = repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));

        fetchedProduct.setName(request.name());
        fetchedProduct.setDescription(request.description());
        fetchedProduct.setPrice(request.price());
        fetchedProduct.setInventory(request.inventory());

        return new ProductResponse(
                fetchedProduct.getId(), fetchedProduct.getName(), fetchedProduct.getDescription(), fetchedProduct.getPrice(), fetchedProduct.getInventory()
        );

    }

    public void deleteById(Long id){
        repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));
        repository.deleteById(id);
    }




}
