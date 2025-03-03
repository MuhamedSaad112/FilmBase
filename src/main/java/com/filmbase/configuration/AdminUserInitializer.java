package com.filmbase.configuration;


import com.filmbase.entity.Authority;
import com.filmbase.entity.User;
import com.filmbase.repository.AuthorityRepository;
import com.filmbase.repository.UserRepository;
import com.filmbase.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes the database with default admin and user accounts.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing default admin and user accounts...");

            // Create default authorities
            Authority adminAuthority = createAuthorityIfNotExists(AuthoritiesConstants.ADMIN);
            Authority userAuthority = createAuthorityIfNotExists(AuthoritiesConstants.USER);

            // Create default users
            List<User> users = List.of(
                    createUser("Mohamed01", "1a41c06ae2@emailvb.pro", "Mohamed", "Saad", "P@ssw0rd12345Secure", adminAuthority),
                    createUser("Mohamed02", "admin2@example.com", "Mohamed1", "Saad2", "P@ssw0rd12345Secure", adminAuthority),
                    createUser("Mohamed03", "e6eb8b9fab@emailvb.pro", "Mohamed", "Saad", "P@ssw0rd12345Secure", userAuthority),
                    createUser("Mohamed04", "user2@example.com", "Mohamed1", "Saad1", "P@ssw0rd12345Secure", userAuthority)
            );

            // Save all users into the database
            userRepository.saveAll(users);

            log.info("Admin and user accounts created successfully.");
        } else {
            log.info("Database already initialized. Skipping user creation.");
        }
    }

    /**
     * Creates an authority if it does not already exist.
     *
     * @param name the name of the authority
     * @return the existing or newly created authority
     */
    private Authority createAuthorityIfNotExists(String name) {
        return authorityRepository.findById(name).orElseGet(() -> {
            Authority authority = new Authority();
            authority.setName(name);
            log.info("Creating authority: {}", name);
            return authorityRepository.save(authority);
        });
    }

    /**
     * Creates a user with the specified details.
     *
     * @param userName     the userName name of the user
     * @param email     the email address of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param password  the raw password of the user
     * @param authority the authority to assign to the user
     * @return the created user
     */
    private User createUser(String userName, String email, String firstName, String lastName, String password, Authority authority) {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password)); // Encode the password
        user.setActivated(true); // Mark user as activated
        user.setResetKey("dummyResetKey"); // Set a dummy reset key
        user.getAuthorities().add(authority);
        log.info("Creating user: {}", userName);
        return user;
    }
}
