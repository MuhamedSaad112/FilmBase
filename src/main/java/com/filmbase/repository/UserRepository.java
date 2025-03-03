package com.filmbase.repository;

import com.filmbase.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link JpaRepository} to provide CRUD operations and custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Cache Names
    String USERS_BY_USER_NAME_CACHE = "usersByUserName";
    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByUserName(String userName);


    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthorityByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthorityByUserName(String userName);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
