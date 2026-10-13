package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.OrderDto;
import org.example.ecommerceapi.exception.OutOfStockException;
import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.OrderRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    public void checkout_Success(){
        User mockedUser = createStandardUser(1L, "test-email", "test-password", Role.USER);
        Product mockedFirstProduct = createStandardProduct(1L, "test-product-A", "test-product-A", BigDecimal.valueOf(100), 10);
        Product mockedSecondProduct = createStandardProduct(2L, "test-product-B", "test-product-B", BigDecimal.valueOf(200), 5);
        CartItem firstCartItem = new CartItem(3, mockedUser, mockedFirstProduct);
        CartItem secondCartItem = new CartItem(5, mockedUser, mockedSecondProduct);
        List<CartItem> items = List.of(firstCartItem, secondCartItem);
        when(userRepository.getUserByEmail(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemsByUser(mockedUser)).thenReturn(items);
        when(productRepository.findByIdWithLock(mockedFirstProduct.getId())).thenReturn(Optional.of(mockedFirstProduct));
        when(productRepository.findByIdWithLock(mockedSecondProduct.getId())).thenReturn(Optional.of(mockedSecondProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto dto = orderService.checkout(mockedUser.getUsername());

        assertThat(dto).isNotNull();
        assertThat(dto.items().size()).isEqualTo(2);
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getStatus()).isEqualByComparingTo(OrderStatus.PENDING);
        assertThat(capturedOrder.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1300));
        verify(cartItemRepository, times(1)).deleteAll(items);
        assertThat(mockedFirstProduct.getInventory()).isEqualTo(7); // 10 - 3 = 7
        assertThat(mockedSecondProduct.getInventory()).isEqualTo(0); // 5 - 5
    }


    @Test
    public void  checkout_With_Empty_Cart(){
        User mockedUser = createStandardUser(1L, "test-email", "test-password", Role.USER);
        List<CartItem> items = List.of();
        when(userRepository.getUserByEmail(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemsByUser(mockedUser)).thenReturn(items);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> orderService.checkout(mockedUser.getUsername()));
        assertThat(exception.getMessage()).contains("Cart is empty");

    }

    @Test
    public void checkout_ThrowsException_WhenOutOfStock(){
        User mockedUser = createStandardUser(1L, "test-email", "test-password", Role.USER);
        Product mockedFirstProduct = createStandardProduct(1L, "test-product-A", "test-product-A", BigDecimal.valueOf(100), 2);
        CartItem cartItem = new CartItem(5, mockedUser, mockedFirstProduct);

        when(userRepository.getUserByEmail(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
        when(productRepository.findByIdWithLock(mockedFirstProduct.getId())).thenReturn(Optional.of(mockedFirstProduct));
        when(cartItemRepository.getCartItemsByUser(mockedUser)).thenReturn(List.of(cartItem));

        OutOfStockException outOfStockException = assertThrows(OutOfStockException.class, () -> orderService.checkout(mockedUser.getUsername()));
        assertThat(outOfStockException.getMessage()).contains("Not enough products on the storage");
        verify(orderRepository, never()).save(any());
        verify(cartItemRepository, never()).deleteAll(any());
        assertThat(mockedFirstProduct.getInventory()).isEqualTo(2);
    }



    private User createStandardUser(Long id, String email, String password, Role role){
        User createdUser = new User(email, password, role);
        createdUser.setId(id);
        return createdUser;
    }

    private Product createStandardProduct(Long id, String name, String description, BigDecimal price, int inventory){
        Product createdProduct = new Product(name, description, price, inventory);
        createdProduct.setId(id);
        return createdProduct;
    }

}
