package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.AuthResponse;
import com.ocr.p3_back.dto.LoginRequest;
import com.ocr.p3_back.dto.RegisterRequest;
import com.ocr.p3_back.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentification", description = "Inscription, connexion et profil de l'utilisateur connecté")
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
    @Operation(summary = "Inscription", description = "Crée un compte et renvoie un token JWT. Route publique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte créé, token renvoyé",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Email déjà utilisé", content = @Content)
    })
    @SecurityRequirements
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
    @Operation(summary = "Connexion", description = "Vérifie email/mot de passe et renvoie un token JWT. Route publique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion OK, token renvoyé",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content)
    })
    @SecurityRequirements
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
    @Operation(summary = "Profil courant", description = "Renvoie le profil de l'utilisateur authentifié (JWT).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil sans mot de passe",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token manquant ou invalide", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        String email = authentication.getName(); // = email mis par le filtre
        return ResponseEntity.ok(authService.me(email));
    }

}
