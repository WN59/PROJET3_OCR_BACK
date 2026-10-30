package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.RentalResponse;
import com.ocr.p3_back.dto.RentalsResponse;
import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.service.RentalService;
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
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> findById(@PathVariable Integer id) {
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseMessage> create(
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("picture") MultipartFile picture,
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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseMessage> update(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") String surface,
            @RequestParam("price") String price,
            @RequestParam("description") String description
    ) {
        return ResponseEntity.ok(
                rentalService.update(id, name, surface, price, description)
        );
    }

}
