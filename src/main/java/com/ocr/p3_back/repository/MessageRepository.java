package com.ocr.p3_back.repository;

import com.ocr.p3_back.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Accès BDD aux messages (CRUD fourni par {@link JpaRepository}).
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /** {@inheritDoc} */
    List<Message> findAll();

    /** {@inheritDoc} */
    Optional<Message> findById(Integer id);
    
}
