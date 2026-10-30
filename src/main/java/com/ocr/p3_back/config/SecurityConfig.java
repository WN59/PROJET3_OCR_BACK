package com.ocr.p3_back.config;

import com.ocr.p3_back.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration Spring Security de l'API.
 * <p>
 * Définit les routes publiques, l'auth JWT (sans session),
 * et l'encodeur BCrypt pour les mots de passe.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * @param jwtAuthenticationFilter filtre qui lit le header Bearer
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Encodeur de mots de passe (BCrypt).
     * Utilisé à l'inscription et à la connexion.
     *
     * @return l'encodeur partagé dans toute l'appli
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Chaîne de filtres de sécurité.
     * <ul>
     *   <li>CSRF désactivé (API JWT, pas de cookie de session)</li>
     *   <li>Session STATELESS : chaque requête apporte son token</li>
     *   <li>register, login et /uploads/** sont publics</li>
     *   <li>le reste exige d'être authentifié</li>
     * </ul>
     *
     * @param http builder Spring Security
     * @return la chaîne de filtres configurée
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/uploads/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
