package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.UserResponse;
import com.ocr.p3_back.entity.User;
import com.ocr.p3_back.exception.ResourceNotFoundException;
import com.ocr.p3_back.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id)
                // Not found sinon ex.getMessage()
                .orElseThrow(() -> new ResourceNotFoundException("error"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}