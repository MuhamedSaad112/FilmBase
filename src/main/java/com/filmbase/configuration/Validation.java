package com.filmbase.configuration;

import lombok.NoArgsConstructor;

/**
 * A utility class to hold validation patterns and constants used across the application.
 */
@NoArgsConstructor
public class Validation {

    // Patterns
    /**
     * Pattern for validating userName. Only alphanumeric characters, length 5-20.
     */
    public static final String USER_NAME_PATTERN = "^[a-zA-Z0-9]{5,50}$";

    /**
     * Pattern for validating password. Requires:
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one digit
     * - At least one special character
     * - Length between 10 and 100
     */
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,100}$";

    // Constants
    /**
     * Default system user.
     */
    public static final String SYSTEM = "system";

    /**
     * Default application language.
     */
    public static final String DEFAULT_LANGUAGE = "en";
}
