package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.CartDto;
import org.example.ecommerceapi.dto.CartItemDto;
import org.example.ecommerceapi.exception.CartItemNotFoundException;
import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Captor
    private ArgumentCaptor<CartItem> cartItemCaptor;

    @Test
    public void addProductSuccessTest() {
        Long productId = 1L;
        Product mockedProduct = new Product("Test-Name", "Test-Description", BigDecimal.valueOf(1000), 10);
        mockedProduct.setId(productId);

        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockedProduct));
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));


        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItemDto result = cartService.addProductToCart(email, productId, 3);

        assertThat(result.productId()).isEqualTo(productId);

        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());
        CartItem savedCartItem = cartItemCaptor.getValue();

        assertThat(savedCartItem.getQuantity()).isEqualTo(3);
        assertThat(savedCartItem.getProduct().getId()).isEqualTo(productId);
        assertThat(savedCartItem.getUser().getUsername()).isEqualTo(email);
    }

    @Test
    public void addProduct_ThrowException_WhenNotEnough() {

        Long productId = 1L;
        Product mockedProduct = new Product("Test-Name", "Test-Description", BigDecimal.valueOf(1000), 1);
        mockedProduct.setId(productId);

        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);


        when(productRepository.findById(productId)).thenReturn(Optional.of(mockedProduct));
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));

        when(cartItemRepository.getCartItemByUserAndProduct(mockedUser, mockedProduct))
                .thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(email, productId, 5));
        assertThat(exception.getMessage()).contains("Not enough inventory");
        verify(cartItemRepository, never()).save(cartItemCaptor.capture());


    }

    @Test
    public void getCart_Success() {
        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);

        Long productAId = 1L;
        Long productBId = 2L;
        Product productA = new Product("Test-Name-A", "Test-Description-A", BigDecimal.valueOf(1000), 10);
        productA.setId(productAId);
        Product productB = new Product("Test-Name-B", "Test-Description-B", BigDecimal.valueOf(100), 5);
        productB.setId(productBId);
        CartItem mockedFirstItem = new CartItem(2, mockedUser, productA);
        CartItem mockedSecondItem = new CartItem(1, mockedUser, productB);
        List<CartItem> items = List.of(mockedFirstItem, mockedSecondItem);

        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemsByUser(mockedUser)).thenReturn(items);

        CartDto dto = cartService.getCart(email);

        assertThat(dto.items().size()).isEqualTo(2);
        assertThat(dto.totalPrice()).isEqualByComparingTo(productA.getPrice().multiply(BigDecimal.TWO).add(productB.getPrice())); // 2 * productA = 2000 and 1 * product


    }

    @Test
    public void getEmptyCart() {
        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);

        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemsByUser(mockedUser)).thenReturn(List.of());
        CartDto dto = cartService.getCart(email);
        assertThat(dto.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.items().size()).isEqualTo(0);
    }

    @Test
    public void removeProduct_Success() {
        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);

        Long productId = 1L;
        Product mockedProduct = new Product("Test-Name", "Test-Description", BigDecimal.valueOf(1000), 1);
        mockedProduct.setId(productId);

        CartItem mockedItem = new CartItem(2, mockedUser, mockedProduct);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockedProduct));
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemByUserAndProduct(mockedUser, mockedProduct)).thenReturn(Optional.of(mockedItem));

        cartService.removeProductFromCart(email, productId);

        verify(cartItemRepository, times(1)).delete(mockedItem);


    }

    @Test
    public void removeProduct_ThrowsCartItemNotFoundException(){
        String email = "testemail@gmail.com";
        User mockedUser = new User(email, "test_password", Role.USER);
        mockedUser.setId(100L);

        Long productId = 1L;
        Product mockedProduct = new Product("Test-Name", "Test-Description", BigDecimal.valueOf(1000), 1);
        mockedProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockedProduct));
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(mockedUser));
        when(cartItemRepository.getCartItemByUserAndProduct(mockedUser, mockedProduct)).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class, () -> cartService.removeProductFromCart(email, productId));
        verify(cartItemRepository, never()).delete(any());
    }
}