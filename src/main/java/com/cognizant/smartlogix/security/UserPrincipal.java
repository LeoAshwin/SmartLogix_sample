package com.cognizant.smartlogix.security;

import com.cognizant.smartlogix.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter that wraps a {@link User} entity and fulfils the Spring Security
 * {@link UserDetails} contract without polluting the JPA entity.
 *
 * <p>Spring Security expects role-based authorities to be prefixed with
 * {@code ROLE_}, e.g. {@code ROLE_DRIVER}.  hasRole("DRIVER") in
 * {@code @PreAuthorize} will match the authority {@code ROLE_DRIVER}.
 */
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final Role role;
    private final boolean active;

    public UserPrincipal(User user) {
        this.userId   = user.getUserId();
        this.email    = user.getEmail();
        this.password = user.getPasswordHash();
        this.role     = user.getRole();
        this.active   = "ACTIVE".equalsIgnoreCase(user.getStatus());
    }

    // ----------------------------------------
    // Custom accessors (used inside the app)
    // ----------------------------------------

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    // ----------------------------------------
    // UserDetails implementation
    // ----------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    /** Spring Security uses getUsername() as the unique identifier. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
