package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.CartDto;
import org.example.ecommerceapi.dto.CartItemDto;
import org.example.ecommerceapi.exception.CartItemNotFoundException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public CartItemDto addProductToCart(String userEmail, Long productId, int quantity) {
        User foundUser = findUserByEmail(userEmail);
        Product foundProduct = findProductById(productId);

        CartItem cartItem = cartItemRepository
                .getCartItemByUserAndProduct(foundUser, foundProduct)
                .orElse(new CartItem(0, foundUser, foundProduct));

        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        cartItemRepository.save(cartItem);

        return new CartItemDto(cartItem.getProduct().getId(), cartItem.getProduct().getName(), cartItem.getProduct().getPrice(), cartItem.getQuantity());

    }

    @Transactional(readOnly = true)
    public CartDto getCart(String userEmail) {
        User foundUser = findUserByEmail(userEmail);
        List<CartItem> items = cartItemRepository.getCartItemsByUser(foundUser);
        List<CartItemDto> dtos = new ArrayList<>(items.size());
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : items){
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            dtos.add(new CartItemDto(item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getPrice(), item.getQuantity()));
        }

        return new CartDto(dtos, totalPrice);
    }

    public void removeProductFromCart(String userEmail, Long productId){
        User foundUser = findUserByEmail(userEmail);
        Product foundProduct = findProductById(productId);
        CartItem foundItem = cartItemRepository
                .getCartItemByUserAndProduct(foundUser, foundProduct)
                .orElseThrow(() -> new CartItemNotFoundException("Cart Item wasn't found"));
        cartItemRepository.delete(foundItem);


    }

    private User findUserByEmail(String userEmail){
        return userRepository.getUserByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User wasn't found"));
    }

    private Product findProductById(Long productId){
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));
    }


}
