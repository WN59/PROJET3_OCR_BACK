package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.RentalResponse;
import com.ocr.p3_back.dto.RentalsResponse;
import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints des locations (rentals).
 * Base URL : {@code /api/rentals} — JWT obligatoire.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Liste, détail, création et mise à jour des rentals (JWT requis)")
public class RentalController {

    private final RentalService rentalService;

    /**
     * @param rentalService logique métier locations
     */
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Liste toutes les locations.
     *
     * @return wrapper {@code { "rentals": [...] }}
     */
    @Operation(summary = "Liste des locations", description = "Renvoie toutes les locations au format { rentals: [...] }.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des rentals",
                    content = @Content(schema = @Schema(implementation = RentalsResponse.class))),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
    @GetMapping
    public ResponseEntity<RentalsResponse> findAll() {
        return ResponseEntity.ok(rentalService.findAll());
    }

    /**
     * Détail d'une location par id.
     *
     * @param id identifiant de la rental
     * @return la location, ou 404 si absente
     */
    @Operation(summary = "Détail d'une location", description = "Renvoie une location par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location trouvée",
                    content = @Content(schema = @Schema(implementation = RentalResponse.class))),
            @ApiResponse(responseCode = "404", description = "Location introuvable", content = @Content),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> findById(
            @Parameter(description = "Identifiant de la location", required = true)
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(rentalService.findById(id));
    }

    /**
     * Crée une location avec image (multipart/form-data).
     * Le propriétaire est l'utilisateur du JWT.
     *
     * @param name          nom de l'annonce
     * @param surface       surface
     * @param price         prix
     * @param picture       fichier image
     * @param description   description
     * @param authentication utilisateur connecté
     * @return {@code { "message": "Rental created !" }}
     */
    @Operation(
            summary = "Créer une location",
            description = "Création en multipart/form-data avec image. Le owner_id vient du JWT, pas du formulaire."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location créée",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseMessage> create(
            @Parameter(description = "Nom de l'annonce", required = true)
            @RequestParam("name") String name,
            @Parameter(description = "Surface", required = true)
            @RequestParam("surface") String surface,
            @Parameter(description = "Prix", required = true)
            @RequestParam("price") String price,
            @Parameter(description = "Image de la location", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("picture") MultipartFile picture,
            @Parameter(description = "Description", required = true)
            @RequestParam("description") String description,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                rentalService.create(name, surface, price, picture, description, email)
        );
    }

    /**
     * Met à jour une location (multipart, sans nouvelle image).
     *
     * @param id          identifiant de la rental
     * @param name        nouveau nom
     * @param surface     nouvelle surface
     * @param price       nouveau prix
     * @param description nouvelle description
     * @return {@code { "message": "Rental updated !" }}
     */
    @Operation(
            summary = "Mettre à jour une location",
            description = "Mise à jour multipart sans champ picture (l'image existante est conservée)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location mise à jour",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "Location introuvable", content = @Content),
            @ApiResponse(responseCode = "403", description = "JWT manquant ou invalide", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseMessage> update(
            @Parameter(description = "Identifiant de la location", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Nom", required = true)
            @RequestParam("name") String name,
            @Parameter(description = "Surface", required = true)
            @RequestParam("surface") String surface,
            @Parameter(description = "Prix", required = true)
            @RequestParam("price") String price,
            @Parameter(description = "Description", required = true)
            @RequestParam("description") String description
    ) {
        return ResponseEntity.ok(
                rentalService.update(id, name, surface, price, description)
        );
    }

}
