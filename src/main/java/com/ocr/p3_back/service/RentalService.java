package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.RentalResponse;
import com.ocr.p3_back.dto.RentalsResponse;
import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.entity.Rental;
import com.ocr.p3_back.entity.User;
import com.ocr.p3_back.exception.ResourceNotFoundException;
import com.ocr.p3_back.repository.RentalRepository;
import com.ocr.p3_back.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Logique métier des locations : liste, détail, création (avec image) et mise à jour.
 */
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /**
     * @param rentalRepository   accès table RENTALS
     * @param userRepository     pour retrouver le propriétaire via l'email JWT
     * @param fileStorageService sauvegarde des images
     */
    public RentalService(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService
    ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Liste toutes les locations au format attendu par le front.
     *
     * @return wrapper {@code { "rentals": [...] }}
     */
    public RentalsResponse findAll() {
        List<RentalResponse> rentals = rentalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return new RentalsResponse(rentals);
    }

    /**
     * Retrouve une location par id.
     *
     * @param id identifiant
     * @return DTO location
     * @throws ResourceNotFoundException si absente
     */
    public RentalResponse findById(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        return toResponse(rental);
    }

    /**
     * Crée une location : owner depuis le JWT, image stockée sur disque.
     *
     * @param name        nom
     * @param surface     surface (texte form-data → BigDecimal)
     * @param price       prix
     * @param picture     fichier image
     * @param description description
     * @param email       email du JWT
     * @return message Mockoon de succès
     */
    public ApiResponseMessage create(
            String name,
            String surface,
            String price,
            MultipartFile picture,
            String description,
            String email
    ) {
        // 1) Qui est connecté ? (email du JWT)
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2) Sauver l'image → URL
        String pictureUrl = fileStorageService.store(picture);

        // 3) Créer la rental en BDD
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(new BigDecimal(surface));
        rental.setPrice(new BigDecimal(price));
        rental.setPicture(pictureUrl);
        rental.setDescription(description);
        rental.setOwnerId(owner.getId());
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());

        rentalRepository.save(rental);

        // 4) Réponse Mockoon
        return new ApiResponseMessage("Rental created !");
    }

    /**
     * Met à jour les infos texte d'une location (sans changer l'image ni l'owner).
     *
     * @param id          identifiant
     * @param name        nom
     * @param surface     surface
     * @param price       prix
     * @param description description
     * @return message Mockoon de succès
     */
    public ApiResponseMessage update(
            Integer id,
            String name,
            String surface,
            String price,
            String description
    ) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        rental.setName(name);
        rental.setSurface(new BigDecimal(surface));
        rental.setPrice(new BigDecimal(price));
        rental.setDescription(description);
        rental.setUpdatedAt(LocalDateTime.now());
        // ne pas toucher : picture, ownerId, createdAt

        rentalRepository.save(rental);
        return new ApiResponseMessage("Rental updated !");
    }

    /**
     * Convertit une Entity Rental en DTO JSON pour le front.
     *
     * @param rental entité BDD
     * @return DTO sans détails techniques JPA
     */
    private RentalResponse toResponse(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getName(),
                rental.getSurface(),
                rental.getPrice(),
                rental.getPicture(),
                rental.getDescription(),
                rental.getOwnerId(),
                rental.getCreatedAt(),
                rental.getUpdatedAt()
        );
    }
}
