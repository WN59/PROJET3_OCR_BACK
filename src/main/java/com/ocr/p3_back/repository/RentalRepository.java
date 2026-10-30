package com.ocr.p3_back.repository;

import com.ocr.p3_back.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Accès BDD aux locations (CRUD fourni par {@link JpaRepository}).
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    /** {@inheritDoc} */
    List<Rental> findAll();

    /** {@inheritDoc} */
    Optional<Rental> findById(Integer id);
}
