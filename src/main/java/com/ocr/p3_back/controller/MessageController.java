package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.dto.MessageRequest;
import com.ocr.p3_back.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints des messages entre utilisateurs et locations.
 * Base URL : {@code /api/messages} — JWT obligatoire.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    /**
     * @param messageService logique métier messages
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Envoie un message lié à une location.
     * L'auteur est l'utilisateur du JWT (pas un user_id du body).
     *
     * @param request        rental_id + texte du message
     * @param authentication utilisateur connecté
     * @return {@code { "message": "Message send with success" }}
     */
    @PostMapping
    public ResponseEntity<ApiResponseMessage> create(
            @Valid @RequestBody MessageRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                messageService.create(
                        request.getRentalId(),
                        request.getMessage(),
                        email
                )
        );
    }
}
