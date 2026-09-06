package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.AuthResponse;
import org.example.ecommerceapi.dto.LoginRequest;
import org.example.ecommerceapi.dto.RegisterRequest;
import org.example.ecommerceapi.exception.UserNotFoundException;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.UserRepository;
import org.example.ecommerceapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        Optional<User> foundUser = userRepository.getUserByEmail(request.email());
        if (foundUser.isPresent()) {
            throw new IllegalArgumentException("User with such an email is already registered!");
        }

        User newUser = new User(request.email(), encoder.encode(request.password()), Role.USER);

        userRepository.save(newUser);


        return new AuthResponse(jwtService.generateToken(newUser));


    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User foundUser = userRepository.getUserByEmail(request.email()).orElseThrow(() -> new UserNotFoundException("User was not found"));

        return new AuthResponse(jwtService.generateToken(foundUser));

    }
}
