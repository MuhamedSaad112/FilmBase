package com.filmbase.security.controller;


import com.filmbase.configuration.Validation;
import com.filmbase.dto.AdminUserDTO;
import com.filmbase.entity.User;
import com.filmbase.exception.BadRequestAlertException;
import com.filmbase.exception.EmailAlreadyUsedException;
import com.filmbase.exception.ResourceNotFoundException;
import com.filmbase.exception.UserNameAlreadyUsedException;
import com.filmbase.repository.UserRepository;
import com.filmbase.security.AuthoritiesConstants;
import com.filmbase.security.service.UserService;
import com.filmbase.service.MailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users. Another option would be to have a
 * specific JPA entity graph to handle this case.
 */

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminUserController {

    @Value("${spring.application.name}")
    private String applicationName;

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(Arrays.asList("id", "userName", "firstName", "lastName", "email", "activated", "langKey", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"));

    /**
     * {@code POST  /admin/users} : Creates a new user.
     * <p>
     * Creates a new user if the userName and email are not already used, and sends an
     * mail with an activation link. The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     * body the new user, or with status {@code 400 (Bad Request)} if the
     * userName or email is already in use.
     * @throws URISyntaxException       if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the userName or
     *                                  email is already in use.
     */

    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {

        log.debug("REST Request to save user: {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID");

        } else if (userRepository.findOneByUserName(userDTO.getUserName().toLowerCase()).isPresent()) {
            throw new UserNameAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {

            throw new EmailAlreadyUsedException();
        } else {

            User newUser = userService.createUser(userDTO);
            mailService.sendPasswordResetEmail(newUser);

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Application-Alert", "User created successfully");
            headers.add("X-Application-User", newUser.getUserName());
            headers.add("X-Application-Status", "Success");

            return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getUserName())).headers(headers).body(newUser);
        }

    }

    /**
     * {@code PUT /admin/users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     * the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is
     *                                   already in use.
     * @throws UserNameAlreadyUsedException {@code 400 (Bad Request)} if the userName is
     *                                   already in use.
     */

    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> updateUser(@Valid @RequestBody AdminUserDTO userDTO) {

        log.debug("REST request to update User : {}", userDTO);

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }

        existingUser = userRepository.findOneByUserName(userDTO.getUserName().toLowerCase());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }

        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);

        // Use map for handling Optional
        return updatedUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK)) // Return 200 if updatedUser is
                // present
                .orElseThrow(() -> new ResourceNotFoundException("User with userName " + userDTO.getUserName() + " not found"));
    }

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this
     * are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     * all users.
     */

    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get all User for an admin");

        // Check if pageable contains only allowed properties
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        // Get the paginated list of users
        final Page<AdminUserDTO> page = userService.getAllManagedUsers(pageable);

        // Debug logs for pagination
        log.debug("Total Elements: {}", page.getTotalElements());
        log.debug("Total Pages: {}", page.getTotalPages());
        log.debug("Current Page: {}", page.getNumber());
        log.debug("Has Next Page: {}", page.hasNext());
        log.debug("Has Previous Page: {}", page.hasPrevious());

        // Create HTTP headers manually for pagination
        HttpHeaders headers = new HttpHeaders();
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();

        // Add Next Page link if there is a next page
        if (page.hasNext()) {
            String nextPageUri = ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber() + 1).replaceQueryParam("size", page.getSize()).toUriString();
            headers.add("X-Next-Page", nextPageUri);
        }

        // Add Previous Page link if there is a previous page
        if (page.hasPrevious()) {
            String previousPageUri = ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber() - 1).replaceQueryParam("size", page.getSize()).toUriString();
            headers.add("X-Prev-Page", previousPageUri);
        }

        // Add additional headers for pagination info
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(page.getNumber()));

        // Return the response with content and pagination headers
        log.debug("Response Headers: {}", headers);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * {@code GET /admin/users/:userName} : get the "userName" user.
     *
     * @param userName the userName of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     * the "userName" user, or with status {@code 404 (Not Found)}.
     */

    @GetMapping("/users/{userName}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> getUser(@PathVariable @Pattern(regexp = Validation.USER_NAME_PATTERN) String userName) {

        log.debug("REST reguest to get User : {}", userName);
        Optional<User> user = userService.getAllWithAuthoritiesByUserName(userName);

        if (user.isPresent()) {
            AdminUserDTO userDTO = new AdminUserDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {

            throw new ResourceNotFoundException("User with userName " + userName + " not found");
            // return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE /admin/users/:userName} : delete the "userName" User.
     *
     * @param userName the userName of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @DeleteMapping("/users/{userName}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable @Pattern(regexp = Validation.USER_NAME_PATTERN) String userName) {

        log.debug("REST request to delete User: {}", userName);

        Optional<User> user = userService.getAllWithAuthoritiesByUserName(userName);
        if (user.isPresent()) {

            userService.deleteUser(userName);
            HttpHeaders headers = new HttpHeaders();
            String alertMessage = "User " + userName + " deleted successfully";
            headers.add("X-Alert", alertMessage);
            return ResponseEntity.ok().headers(headers).build();
        } else {

            throw new ResourceNotFoundException("User with userName " + userName + " not found");
        }

    }

}