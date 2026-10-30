package com.ocr.p3_back.service;

import com.ocr.p3_back.dto.UserResponse;
import com.ocr.p3_back.entity.User;
import com.ocr.p3_back.exception.ResourceNotFoundException;
import com.ocr.p3_back.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Logique métier lecture d'un utilisateur par id (profil propriétaire).
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * @param userRepository accès table USERS
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrouve un user et le mappe en DTO (sans password).
     *
     * @param id identifiant
     * @return profil JSON
     * @throws ResourceNotFoundException si absent
     */
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
