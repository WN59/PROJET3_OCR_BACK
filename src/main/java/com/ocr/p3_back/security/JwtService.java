package com.ocr.p3_back.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service JWT : crée, lit et valide les tokens.
 * Le subject du token contient l'email de l'utilisateur.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * @param secret       clé secrète (JWT_SECRET)
     * @param expirationMs durée de vie du token en millisecondes
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Génère un token signé pour un email (après login / register).
     *
     * @param email email de l'utilisateur
     * @return le JWT (chaîne à renvoyer au front)
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrait l'email (subject) d'un token déjà signé.
     *
     * @param token JWT reçu dans le header Authorization
     * @return l'email contenu dans le token
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Vérifie que le token est bien signé et pas encore expiré.
     *
     * @param token JWT à contrôler
     * @return {@code true} si le token est utilisable
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse et vérifie la signature du token.
     *
     * @param token JWT brut
     * @return les claims (subject, expiration, etc.)
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
