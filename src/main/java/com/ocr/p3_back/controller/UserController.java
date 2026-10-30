package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.UserResponse;
import com.ocr.p3_back.service.UserService;
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
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
