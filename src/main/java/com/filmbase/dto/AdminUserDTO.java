package com.filmbase.dto;



import com.filmbase.entity.Authority;
import com.filmbase.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with their authorities.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AdminUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User ID.
     */
    private Long id;

    /**
     * User userName name. Must not be blank and has a maximum size of 60 characters.
     */

    @NotNull(message = "userName cannot be Null.")
    @Size(min = 5, max = 50)
    private String userName;

    /**
     * User's first name.
     */
    @Size(max = 50)
    private String firstName;

    /**
     * User's last name.
     */
    @Size(max = 50)
    private String lastName;

    /**
     * User's email. Must be valid and within the defined size limits.
     */
    @Email
    @Size(min = 10, max = 254)
    private String email;

    /**
     * URL of the user's profile image.
     */
    @Size(max = 255)
    private String imageUrl;

    /**
     * User who created this entity.
     */
    private String createdBy;

    /**
     * Timestamp of when this entity was created.
     */
    private Instant createdDate;

    /**
     * Language key for localization.
     */
    private String langKey;

    /**
     * User who last modified this entity.
     */
    private String lastModifiedBy;

    /**
     * Timestamp of the last modification of this entity.
     */
    private Instant lastModifiedDate;

    /**
     * Whether the user is activated or not.
     */
    private boolean activated = false;

    /**
     * Set of authorities assigned to the user.
     */
    private Set<String> authorities;

    /**
     * Constructor to create a DTO from a `User` entity.
     *
     * @param user the user entity
     */
    public AdminUserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.langKey = user.getLangKey();
        this.email = user.getEmail();
        this.activated = user.isActivated();
        this.imageUrl = user.getImageUrl();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
