package com.filmbase.security.service;

import com.filmbase.configuration.Validation;
import com.filmbase.dto.AdminUserDTO;
import com.filmbase.dto.UserResponseDto;
import com.filmbase.entity.Authority;
import com.filmbase.entity.User;
import com.filmbase.exception.EmailAlreadyUsedException;
import com.filmbase.exception.InvalidPasswordException;
import com.filmbase.exception.UserNameAlreadyUsedException;
import com.filmbase.repository.AuthorityRepository;
import com.filmbase.repository.UserRepository;
import com.filmbase.security.AuthoritiesConstants;
import com.filmbase.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    // Account Controller
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);

        return userRepository.findOneByActivationKey(key).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            this.clearUserCaches(user);
            log.debug("Activated User :{}", user);
            return user;
        });
    }

    // Account Controller
    public Optional<User> completePasswordReset(String newPassword, String key) {


        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key).filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS))).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetDate(null);
            user.setResetKey(null);
            this.clearUserCaches(user);
            return user;

        });

    }

    // Account Controller
    public Optional<User> requestPasswordReset(String mail) {
        log.debug("Searching for email: {}", mail);

        return userRepository.findOneByEmailIgnoreCase(mail).filter(User::isActivated).map(user -> {
            log.debug("User found and activated: {}", user.getEmail());
            user.setResetKey(RandomUtil.generateResetKey().substring(0, 20));
            user.setResetDate(Instant.now());
            this.clearUserCaches(user);
            return user;

        });
    }

    // Account Controller
    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository.findOneByUserName(userDTO.getUserName().toLowerCase()).ifPresent(existingUser -> {

            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UserNameAlreadyUsedException();
            }

        });

        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {

            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }

        });

        User newUser = new User();

        String encryptedPassword = passwordEncoder.encode(password);

        newUser.setUserName(userDTO.getUserName().toLowerCase());

        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }

        if (userDTO.getLangKey() == null) {
            newUser.setLangKey(Validation.DEFAULT_LANGUAGE);
        } else {

            newUser.setLangKey(userDTO.getLangKey());
        }

        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey().substring(0, 20));

        Set<Authority> authorities = new HashSet<>();

        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;

    }

    private Boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    // Admin Controller
    public User createUser(AdminUserDTO userDTO) {

        User user = new User();

        user.setUserName(userDTO.getUserName().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getLangKey() == null) {
            user.setLangKey(Validation.DEFAULT_LANGUAGE);// default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey().substring(0, 20));
        user.setResetDate(Instant.now());
        user.setActivated(true);

        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream().map(authorityRepository::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }

        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    // Admin Controller
//    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
//
//        return Optional.of(userRepository.findById(userDTO.getId())).filter(Optional::isPresent).map(Optional::get).map(user -> {
//
//            user.setLogin(userDTO.getLogin());
//            user.setFirstName(userDTO.getFirstName());
//            user.setLastName(userDTO.getLastName());
//            if (userDTO.getEmail() != null) {
//                user.setEmail(userDTO.getEmail());
//            }
//            user.setImageUrl(userDTO.getImageUrl());
//            user.setActivated(userDTO.isActivated());
//            user.setLangKey(userDTO.getLangKey());
//            Set<Authority> authorities = user.getAuthorities();
//
//            userDTO.getAuthorities().stream().map(authorityRepository::findById).filter(Optional::isEmpty).map(Optional::get).forEach(authorities::add);
//            this.clearUserCaches(user);
//            log.debug("Changed Information for User: {}", user);
//            return user;
//        }).map(AdminUserDTO::new);
//
//    }


    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {

        return Optional.of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {

                    user.setUserName(userDTO.getUserName());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail());
                    }
                    user.setImageUrl(userDTO.getImageUrl());
                    user.setActivated(userDTO.isActivated());
                    user.setLangKey(userDTO.getLangKey());

                    Set<Authority> authorities = user.getAuthorities();

                    if (authorities == null) {
                        authorities = new HashSet<>();
                        user.setAuthorities(authorities);
                    }

                    if (userDTO.getAuthorities() != null) {
                        userDTO.getAuthorities().stream()
                                .map(authorityRepository::findById)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .forEach(authorities::add);
                    }

                    this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                }).map(AdminUserDTO::new);
    }


    // Admin Controller
    public void deleteUser(String userName) {

        userRepository.findOneByUserName(userName).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Delete   User: {}", user);
        });
    }

    // Account Controller
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {

        SecurityUtils.getCurrentUserUserName().flatMap(userRepository::findOneByUserName).ifPresent(user -> {

            user.setFirstName(firstName);
            user.setLastName(lastName);
            if (email != null) {
                user.setEmail(email);
            }

            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            this.clearUserCaches(user);
            log.debug("Changed Information for User: {}", user);
        });

    }

    // Account Controller
    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {

        SecurityUtils.getCurrentUserUserName().flatMap(userRepository::findOneByUserName).ifPresent(user -> {

            String currentEncryptedPassword = user.getPassword();

            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                throw new InvalidPasswordException();
            }

            String EncryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(EncryptedPassword);
            this.clearUserCaches(user);
            log.debug("Changed password for User: {}", user);

        });

    }

    // Admin Controller
  //  @Cacheable(value = "users", key = "'all-users'")
    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserResponseDto::new);
    }

    // Admin Controller
    @Transactional(readOnly = true)
    public Optional<User> getAllWithAuthoritiesByUserName(String userName) {
        return userRepository.findOneWithAuthorityByUserName(userName);
    }

    // Account Controller
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserUserName().flatMap(userRepository::findOneWithAuthorityByUserName);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(30, ChronoUnit.DAYS)).forEach(user -> {
            log.debug("Deleting not activated user {}", user.getUserName());
            userRepository.delete(user);
            this.clearUserCaches(user);
        });

    }

    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_USER_NAME_CACHE)).evict(user.getUserName());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

}

/**
 * Utility class for generating random values.
 */
class RandomUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_LENGTH = 20;

    public static String generateRandomKey(int length) {
        byte[] randomBytes = new byte[length];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String generateResetKey() {
        return generateRandomKey(DEFAULT_LENGTH);
    }

    public static String generateActivationKey() {
        return generateRandomKey(DEFAULT_LENGTH);
    }

    public static String generatePassword() {
        return generateRandomKey(DEFAULT_LENGTH);
    }
}