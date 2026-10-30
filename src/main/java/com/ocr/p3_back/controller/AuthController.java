package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.AuthResponse;
import com.ocr.p3_back.dto.LoginRequest;
import com.ocr.p3_back.dto.RegisterRequest;
import com.ocr.p3_back.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import com.ocr.p3_back.dto.UserResponse;

/**
 * Endpoints d'authentification : inscription, connexion et profil courant.
 * Base URL : {@code /api/auth}
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * @param authService logique métier auth
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Crée un compte et renvoie un JWT.
     * Route publique.
     *
     * @param request name, email, password
     * @return {@code { "token": "..." }}
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Connecte un utilisateur et renvoie un JWT.
     * Route publique.
     *
     * @param request email, password
     * @return {@code { "token": "..." }}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Renvoie le profil de l'utilisateur connecté.
     * Nécessite un Bearer JWT.
     *
     * @param authentication contexte Security (email = getName())
     * @return profil sans mot de passe
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        String email = authentication.getName(); // = email mis par le filtre
        return ResponseEntity.ok(authService.me(email));
    }

}
