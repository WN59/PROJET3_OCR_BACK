package com.ocr.p3_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ocr.p3_back.entity.User;
import java.util.Optional;

/**
 * Accès BDD aux utilisateurs.
 * Spring Data génère les requêtes à partir des noms de méthodes.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Cherche un utilisateur par email.
     *
     * @param email email unique
     * @return utilisateur éventuel
     */
    Optional<User> findByEmail(String email);

    /**
     * Indique si un email est déjà pris (inscription).
     *
     * @param email email à tester
     * @return {@code true} si déjà présent
     */
    boolean existsByEmail(String email);
    
}
