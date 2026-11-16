package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.CartDto;
import org.example.ecommerceapi.dto.CartItemDto;
import org.example.ecommerceapi.exception.*;
import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartItemDto addProductToCart(User user, Long productId, int quantity) {
        Product foundProduct = findProductById(productId);

        CartItem cartItem = cartItemRepository
                .getCartItemByUserAndProduct(user, foundProduct)
                .orElse(new CartItem(0, user, foundProduct));

        int newQuantity = cartItem.getQuantity() + quantity;


        if (foundProduct.getInventory() < newQuantity) {
            throw new OutOfStockException("Not enough inventory for product: " + foundProduct.getName());
        }

        cartItem.setQuantity(newQuantity);

        cartItemRepository.save(cartItem);

        return new CartItemDto(productId, foundProduct.getName(), foundProduct.getPrice(), cartItem.getQuantity());

    }

    @Transactional(readOnly = true)
    public CartDto getCart(User user) {
        List<CartItem> items = cartItemRepository.getCartItemsByUser(user);
        List<CartItemDto> dtos = new ArrayList<>(items.size());
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : items){
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            dtos.add(new CartItemDto(item.getProduct().getId(), item.getProduct().getName(), item.getProduct().getPrice(), item.getQuantity()));
        }

        return new CartDto(dtos, totalPrice);
    }

    public void removeProductFromCart(User user, Long productId){
        Product foundProduct = findProductById(productId);
        CartItem foundItem = cartItemRepository
                .getCartItemByUserAndProduct(user, foundProduct)
                .orElseThrow(() -> new CartItemNotFoundException("Cart Item wasn't found"));
        cartItemRepository.delete(foundItem);


    }

    private Product findProductById(Long productId){
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product wasn't found"));
    }


}
