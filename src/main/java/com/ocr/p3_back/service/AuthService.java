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

/**
 * Logique métier de l'authentification :
 * inscription, connexion (BCrypt) et profil {@code /me}.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * @param userRepository  accès table USERS
     * @param passwordEncoder encodeur BCrypt
     * @param jwtService      création des tokens
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Inscrit un nouvel utilisateur et renvoie un JWT.
     *
     * @param request données d'inscription
     * @return token JWT
     * @throws BadCredentialsException si l'email existe déjà
     */
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

    /**
     * Vérifie email + mot de passe et renvoie un JWT.
     *
     * @param request identifiants
     * @return token JWT
     * @throws BadCredentialsException si email ou mot de passe incorrect
     */
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

    /**
     * Charge le profil de l'utilisateur connecté (sans password).
     *
     * @param email email issu du JWT
     * @return DTO profil
     */
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
