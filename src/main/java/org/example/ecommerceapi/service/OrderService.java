package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.OrderDto;
import org.example.ecommerceapi.dto.OrderItemDto;
import org.example.ecommerceapi.exception.OutOfStockException;
import org.example.ecommerceapi.exception.UserNotFoundException;
import org.example.ecommerceapi.model.*;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.OrderRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartItemRepository cartItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public OrderDto checkout(String userEmail) {
        User fetchedUser = userRepository.getUserByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User wasn't found"));

        List<CartItem> items = cartItemRepository.getCartItemsByUser(fetchedUser);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty!");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Order createdOrder = new Order(totalPrice, OrderStatus.PENDING, LocalDateTime.now(), fetchedUser);

        for (CartItem item : items) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getInventory()) {
                throw new OutOfStockException("Not enough products on the storage");
            }

            product.setInventory(product.getInventory() - item.getQuantity());

            OrderItem orderItem = new OrderItem(item.getQuantity(), product.getPrice(), createdOrder, product);
            createdOrder.addItem(orderItem);
            totalPrice = totalPrice.add(
                    orderItem.getPrice().multiply(
                            BigDecimal.valueOf(orderItem.getQuantity())
                    )
            );
        }

        createdOrder.setTotalPrice(totalPrice);
        orderRepository.save(createdOrder);
        cartItemRepository.deleteAll(items);

        List<OrderItemDto> orderItemDtos = createdOrder.getItems().stream()
                .map(
                        item -> new OrderItemDto(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity()
                        )
                )
                .toList();

        return new OrderDto(
                createdOrder.getId(),
                createdOrder.getTotalPrice(),
                createdOrder.getStatus(),
                createdOrder.getCreatedAt(),
                orderItemDtos
        );

    }


}
