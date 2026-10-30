package com.ocr.p3_back.dto;

/**
 * Réponse login / register : {@code { "token": "..." }}.
 */
public class AuthResponse {

    private String token;

    public AuthResponse() {}

    /**
     * @param token JWT à renvoyer au client
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}