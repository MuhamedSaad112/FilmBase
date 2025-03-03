package com.filmbase.repository;

import com.filmbase.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Authority} entities.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    /**
     * Find an authority by its name.
     *
     * @param name the name of the authority
     * @return an {@link Optional} containing the authority if found, or empty if not found
     */
    Optional<Authority> findByName(String name);

    /**
     * Check if an authority exists by its name.
     *
     * @param name the name of the authority
     * @return true if the authority exists, false otherwise
     */
    boolean existsByName(String name);
}
