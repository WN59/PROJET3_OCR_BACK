package com.ocr.p3_back.dto;

/**
 * Réponse simple avec un message texte
 * (create/update rental, envoi de message…).
 */
public class ApiResponseMessage {

    private String message;

    public ApiResponseMessage() {}

    /**
     * @param message texte affiché / attendu par le front (ex. {@code Rental created !})
     */
    public ApiResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}