package com.filmbase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO for handling password change requests.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequestDto {

    /**
     * The current password of the user.
     * Must not be blank and should be between 10 and 100 characters long.
     */
    @NotBlank(message = "Current password must not be blank.")
    @Size(min = 10, max = 100, message = "Current password must be between 10 and 100 characters.")
    private String currentPassword;

    /**
     * The new password for the user.
     * Must not be blank and should be between 10 and 100 characters long.
     */
    @NotBlank(message = "New password must not be blank.")
    @Size(min = 10, max = 100, message = "New password must be between 10 and 100 characters.")
    private String newPassword;

}
