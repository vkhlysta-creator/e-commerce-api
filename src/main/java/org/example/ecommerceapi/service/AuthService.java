package org.example.ecommerceapi.service;

import org.example.ecommerceapi.dto.AuthResponse;
import org.example.ecommerceapi.dto.LoginRequest;
import org.example.ecommerceapi.dto.RegisterRequest;
import org.example.ecommerceapi.exception.EmailAlreadyRegisteredException;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.UserRepository;
import org.example.ecommerceapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
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
            throw new EmailAlreadyRegisteredException();
        }

        User newUser = new User(request.email(), encoder.encode(request.password()), Role.USER);

        userRepository.save(newUser);


        return new AuthResponse(jwtService.generateToken(newUser));


    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        if (authentication.getPrincipal() instanceof User user) {
            return new AuthResponse(jwtService.generateToken(user));
        }
        throw new IllegalStateException("Authenticated Object is not USER");

    }
}
