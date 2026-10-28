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

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public RentalService(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService
    ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public RentalsResponse findAll() {
        List<RentalResponse> rentals = rentalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return new RentalsResponse(rentals);
    }

    public RentalResponse findById(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        return toResponse(rental);
    }

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