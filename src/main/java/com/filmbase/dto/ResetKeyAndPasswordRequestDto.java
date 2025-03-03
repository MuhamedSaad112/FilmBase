package com.filmbase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for transferring the reset key and the new password.
 */
@Getter
@Setter
public class ResetKeyAndPasswordRequestDto {

    /**
     * The reset key provided to the user.
     * Must not be blank.
     */
    @NotBlank(message = "Reset key must not be blank.")
    private String key;

    /**
     * The new password for the user.
     * Must meet the minimum and maximum length requirements.
     */
    @Size(min = 10, max = 100,
            message = "Password must be between 10 and 100 characters long.")
    private String newPassword;

}
