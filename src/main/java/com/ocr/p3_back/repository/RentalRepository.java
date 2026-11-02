package com.ocr.p3_back.repository;

import com.ocr.p3_back.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès BDD aux locations (CRUD fourni par {@link JpaRepository}).
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {
}
