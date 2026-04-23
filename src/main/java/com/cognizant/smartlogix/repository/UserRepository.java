package com.cognizant.smartlogix.repository;

import com.cognizant.smartlogix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for the {@link User} entity.
 *
 * <p>Extends {@link JpaRepository} which provides standard CRUD operations.
 * The custom {@link #findByEmail(String)} query is used by Spring Security's
 * {@link com.cognizant.smartlogix.security.CustomUserDetailsService} during
 * authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for (case-sensitive)
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);
}
