package com.cognizant.smartlogix.security;

import com.cognizant.smartlogix.model.User;
import com.cognizant.smartlogix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * <p>Two loading strategies are provided:
 * <ul>
 *   <li>{@link #loadUserByUsername(String)} — looks up by email (used during login)</li>
 *   <li>{@link #loadByUserId(Long)} — looks up by PK (used inside JWT filter)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by email address.
     * Called by Spring Security's {@code AuthenticationManager} during login.
     *
     * @param email the user's email (mapped as the Spring Security "username")
     * @throws UsernameNotFoundException when no user with that email exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + email));
        return new UserPrincipal(user);
    }

    /**
     * Loads a user by primary key — called by {@link JwtAuthenticationFilter}
     * on every authenticated request.
     *
     * @param userId the user's primary key extracted from the JWT subject claim
     * @throws UsernameNotFoundException when no user with that ID exists
     */
    @Transactional(readOnly = true)
    public UserPrincipal loadByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with id: " + userId));
        return new UserPrincipal(user);
    }
}
