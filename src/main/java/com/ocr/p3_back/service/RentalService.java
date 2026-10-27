package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.RentalResponse;
import com.ocr.p3_back.dto.RentalsResponse;
import com.ocr.p3_back.entity.Rental;
import com.ocr.p3_back.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public RentalsResponse findAll() {
        List<RentalResponse> rentals = rentalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return new RentalsResponse(rentals);
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