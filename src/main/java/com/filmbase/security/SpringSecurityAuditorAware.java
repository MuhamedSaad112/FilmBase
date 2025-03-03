package com.filmbase.security;

import com.filmbase.configuration.Validation;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} to determine the current auditor (user)
 * for auditing purposes based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    /**
     * Returns the current auditor (user) based on Spring Security.
     * If no user is authenticated, the system identifier is returned.
     *
     * @return an {@link Optional} containing the current user's userName or the system identifier
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserUserName().orElse(Validation.SYSTEM));
    }
}
