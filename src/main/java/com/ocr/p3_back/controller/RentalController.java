package com.ocr.p3_back.controller;

import com.ocr.p3_back.dto.RentalsResponse;
import com.ocr.p3_back.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<RentalsResponse> findAll() {
        return ResponseEntity.ok(rentalService.findAll());
    }
}