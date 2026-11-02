package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.UserResponse;
import com.ocr.p3_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints utilisateurs (profil public d'un propriétaire).
 * Base URL : {@code /api/user} — JWT obligatoire.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "Utilisateurs", description = "Consultation d'un profil utilisateur par id (JWT requis)")
public class UserController {

    private final UserService userService;

    /**
     * @param userService logique métier user
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Renvoie un utilisateur par son id (sans mot de passe).
     *
     * @param id identifiant user
     * @return profil, ou 404 si absent
     */
    @Operation(summary = "Profil par id", description = "Renvoie un utilisateur sans mot de passe (ex. propriétaire d'une location).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "Identifiant de l'utilisateur", required = true)
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
