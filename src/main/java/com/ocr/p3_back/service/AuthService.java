package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.AuthResponse;
import com.ocr.p3_back.dto.LoginRequest;
import com.ocr.p3_back.dto.RegisterRequest;
import com.ocr.p3_back.dto.UserResponse;
import com.ocr.p3_back.entity.User;
import com.ocr.p3_back.repository.UserRepository;
import com.ocr.p3_back.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            // Email already exists
            throw new BadCredentialsException("error");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                // Invalid email
                .orElseThrow(() -> new BadCredentialsException("error"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Invalid credentials
            throw new BadCredentialsException("error");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public UserResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("error"));
    
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}