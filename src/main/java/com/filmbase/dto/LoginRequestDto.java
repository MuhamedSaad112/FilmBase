package com.filmbase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing the login credentials for a user.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

    /**
     * The username  identifier.
     * Must not be null or blank and should be between 5 and 50 characters long.
     */
    @NotNull(message = "Username must not be null.")
    @NotBlank(message = "Username must not be blank.")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters.")
    private String userName;

    /**
     * The user's password.
     * Must not be null or blank and should be between 5 and 100 characters long.
     */
    @NotNull(message = "Password must not be null.")
    @NotBlank(message = "Password must not be blank.")
    @Size(min = 5, max = 100, message = "Password must be between 5 and 100 characters.")
    private String password;

    /**
     * Indicates if the user wants to be remembered on this device.
     * Defaults to false.
     */
    private boolean rememberMe = false;

}
