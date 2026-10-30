package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.ApiResponseMessage;
import com.ocr.p3_back.entity.Message;
import com.ocr.p3_back.entity.User;
import com.ocr.p3_back.exception.ResourceNotFoundException;
import com.ocr.p3_back.repository.MessageRepository;
import com.ocr.p3_back.repository.RentalRepository;
import com.ocr.p3_back.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            RentalRepository rentalRepository
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public ApiResponseMessage create(Integer rentalId, String messageText, String email) {
        // 1) Qui écrit ? (JWT)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2) La rental existe ?
        rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));

        // 3) Sauver le message
        Message message = new Message();
        message.setRentalId(rentalId);
        message.setUserId(user.getId()); // jamais depuis le body client
        message.setMessage(messageText);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);

        // 4) Réponse Mockoon (orthographe exacte)
        return new ApiResponseMessage("Message send with success");
    }
}