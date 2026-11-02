package com.ocr.p3_back.repository;

import com.ocr.p3_back.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès BDD aux messages (CRUD fourni par {@link JpaRepository}).
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
