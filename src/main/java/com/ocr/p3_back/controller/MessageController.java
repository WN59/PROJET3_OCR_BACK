package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.dto.MessageRequest;
import com.ocr.p3_back.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Messages", description = "Envoi de messages liés à une location (JWT requis)")
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
    @Operation(
            summary = "Envoyer un message",
            description = "Body JSON avec rental_id et message. L'auteur (user_id) est déduit du JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message enregistré",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "Location ou utilisateur introuvable", content = @Content),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
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
