package com.ocr.p3_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.Map;

/**
 * Traduit les exceptions métier en réponses HTTP JSON
 * alignées sur le contrat Mockoon / front.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erreur de validation {@code @Valid} → 400 et body vide.
     *
     * @param ex exception de validation
     * @return {@code {}}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of());
    }

    /**
     * Identifiants incorrects (login / register) → 401.
     *
     * @param ex exception Spring Security
     * @return {@code { "message": "error" }}
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                // Bad credentials sinon ex.getMessage()
                .body(Map.of("message", "error"));
    }

    /**
     * Ressource introuvable → 404.
     *
     * @param ex exception métier
     * @return {@code { "message": "error" }}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                // Not found sinon ex.getMessage()
                .body(Map.of("message", "error"));
    }

}
