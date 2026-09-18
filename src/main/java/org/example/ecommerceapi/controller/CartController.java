package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.dto.CartDto;
import org.example.ecommerceapi.dto.CartItemDto;
import org.example.ecommerceapi.dto.CartItemRequest;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addInCart(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(user.getUsername(), request.productId(), request.quantity()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId
    ){
        cartService.removeProductFromCart(user.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }


}
