package com.filmbase.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO for managing users, extending {@link AdminUserDTO}.
 * Includes additional validation for the password field.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterUserRequestDto extends AdminUserDTO {

    /**
     * Minimum length for the password.
     */
    public static final int PASSWORD_MIN_LENGTH = 10;

    /**
     * Maximum length for the password.
     */
    public static final int PASSWORD_MAX_LENGTH = 100;

    /**
     * Password for the user. Must meet the length constraints defined.
     */
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters long.")
    private String password;

}
