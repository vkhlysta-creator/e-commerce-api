package org.example.ecommerceapi.service;

import org.example.ecommerceapi.exception.ProductNotFoundException;
import org.example.ecommerceapi.exception.UserNotFoundException;
import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addProductToCart(String userEmail, Long productId, int quantity){
        User foundUser = userRepository.getUserByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User wasn't found"));
        Product foundProduct = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));

        CartItem cartItem = cartItemRepository
                .getCartItemByUserAndProduct(foundUser, foundProduct)
                .orElse(new CartItem(0, foundUser, foundProduct));

        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        cartItemRepository.save(cartItem);

    }


}
