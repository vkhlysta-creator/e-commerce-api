package org.example.ecommerceapi.controller;

import jakarta.validation.Valid;
import org.example.ecommerceapi.dto.ProductRequest;
import org.example.ecommerceapi.dto.ProductResponse;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request){
        return ResponseEntity.ok(productService.createNewProduct(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> findByNameContaining(@RequestParam("query") String query){
        return ResponseEntity.ok(productService.findByNameContaining(query));
    }
}
