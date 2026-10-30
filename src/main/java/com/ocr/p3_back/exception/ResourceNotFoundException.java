package com.ocr.p3_back.exception;

/**
 * Exception métier : ressource introuvable (rental, user…).
 * Attrapée par {@link GlobalExceptionHandler} → HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message détail pour les logs / debug
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
