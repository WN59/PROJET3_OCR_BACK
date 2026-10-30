package com.ocr.p3_back.repository;

import com.ocr.p3_back.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAll();

    Optional<Message> findById(Integer id);
    
}