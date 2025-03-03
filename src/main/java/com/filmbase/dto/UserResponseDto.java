package com.filmbase.dto;

import com.filmbase.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Data Transfer Object (DTO) representing a simplified version of the `User` entity.
 * Typically used for lightweight responses where only basic user details are needed.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The  username of the user.
     */
    private String userName;

    /**
     * Constructs a `UserDto` from a `User` entity.
     *
     * @param user the `User` entity to map from.
     */
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
    }
}
