package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.AuthResponse;
import org.example.ecommerceapi.dto.LoginRequest;
import org.example.ecommerceapi.dto.RegisterRequest;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.UserRepository;
import org.example.ecommerceapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    public void register_Success(){
        RegisterRequest registerRequest = createStandardRegisterRequest("test-mail", "raw-password");

        when(userRepository.getUserByEmail(registerRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded_password_hash");
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked_jwt_token");
        AuthResponse response = authService.register(registerRequest);


        assertThat(response.token()).isEqualTo("mocked_jwt_token");
        verify(userRepository).save(userCaptor.capture());
        User registeredUser = userCaptor.getValue();
        assertThat(registeredUser.getUsername()).isEqualTo(registerRequest.email());
        assertThat(registeredUser.getPassword()).isEqualTo("encoded_password_hash");
        assertThat(registeredUser.getRole()).isEqualTo(Role.USER);

    }

    @Test
    public void register_ThrowsException_WhenEmailAlreadyExists(){
        RegisterRequest registerRequest = createStandardRegisterRequest("test-mail-used", "raw-password-used");

        when(userRepository.getUserByEmail(registerRequest.email())).thenReturn(Optional.of(new User(registerRequest.email(), "encoded-password", Role.USER)));

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));

    }

    @Test
    public void authenticate_Success(){
        LoginRequest loginRequest = createStandardLoginRequest("test-mail", "test-password");
        User testUser = new User("test-mail", "test-password-encoded", Role.USER);

        when(userRepository.getUserByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("jwt_token");

        AuthResponse authResponse = authService.authenticate(loginRequest);

        assertThat(authResponse.token()).isEqualTo("jwt_token");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

    }


    private RegisterRequest createStandardRegisterRequest(String email, String password){
        return new RegisterRequest(email, password);
    }

    private LoginRequest createStandardLoginRequest(String email, String password){
        return new LoginRequest(email, password);
    }
}
