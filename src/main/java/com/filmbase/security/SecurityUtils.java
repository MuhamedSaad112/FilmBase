package com.filmbase.security;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security-related operations.
 */
@NoArgsConstructor
public final class SecurityUtils {

    /**
     * Extracts the principal (username) from the given Authentication object.
     *
     * @param authentication the Authentication object.
     * @return the username of the principal if it exists; null otherwise.
     */
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Retrieves the userName (username) of the currently authenticated user.
     *
     * @return an Optional containing the current user's userName, or empty if not available.
     */
    public static Optional<String> getCurrentUserUserName() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(SecurityUtils::extractPrincipal);
    }

    /**
     * Retrieves the JWT token of the currently authenticated user.
     *
     * @return an Optional containing the JWT token, or empty if not available.
     */
    public static Optional<String> getCurrentJWT() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth.getCredentials() instanceof String)
                .map(auth -> (String) auth.getCredentials());
    }

    /**
     * Retrieves the authorities of the currently authenticated user.
     *
     * @param authentication the Authentication object.
     * @return a Stream of authorities.
     */
    private static Stream<String> getAuthorities(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(authorities -> authorities.stream().map(GrantedAuthority::getAuthority));
    }

    /**
     * Determines if the current user is authenticated.
     *
     * @return true if the user is authenticated and not anonymous; false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    }

    /**
     * Checks if the current user has any of the specified authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the user has any of the authorities; false otherwise.
     */
    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).anyMatch(Arrays.asList(authorities)::contains);
    }

    /**
     * Checks if the current user has none of the specified authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the user has none of the authorities; false otherwise.
     */
    public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
        return !hasCurrentUserAnyOfAuthorities(authorities);
    }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the user has the authority; false otherwise.
     */
    public static boolean hasCurrentUserThisAuthority(String authority) {
        return hasCurrentUserAnyOfAuthorities(authority);
    }
}
