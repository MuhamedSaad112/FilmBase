package com.filmbase.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * Represents a specific authority (role) assigned to a user.
 * Example: ROLE_USER, ROLE_ADMIN.
 */
@Entity
@Table(name = "sec_authority")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The unique name of the authority.
     */
    @Id
    @NotNull(message = "Authority name cannot be null.")
    @Size(max = 50, message = "Authority name must not exceed 50 characters.")
    @Column(length = 50, nullable = false, unique = true)
    private String name;
}
