package com.filmbase.security.controller;


import com.filmbase.dto.AdminUserDTO;
import com.filmbase.dto.PasswordChangeRequestDto;
import com.filmbase.dto.RegisterUserRequestDto;
import com.filmbase.dto.ResetKeyAndPasswordRequestDto;
import com.filmbase.entity.User;
import com.filmbase.exception.EmailAlreadyUsedException;
import com.filmbase.exception.InvalidPasswordException;
import com.filmbase.exception.ResourceNotFoundException;
import com.filmbase.exception.UserNameAlreadyUsedException;
import com.filmbase.repository.UserRepository;
import com.filmbase.security.SecurityUtils;
import com.filmbase.security.service.UserService;
import com.filmbase.service.MailService;
import com.filmbase.service.ProfileImageUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final ProfileImageUpdateService profileImageUpdateService;


    private static class AccountControllerException extends RuntimeException {

        private AccountControllerException(String message) {
            super(message);
        }
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param registerUserRequestDto the managed user View Model.
     * @throws InvalidPasswordException  {@code 400 (Bad Request)} if the password
     *                                   is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is
     *                                   already used.
     * @throws UserNameAlreadyUsedException {@code 400 (Bad Request)} if the userName is
     *                                   already used.
     */

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody RegisterUserRequestDto registerUserRequestDto) {

        log.debug("Registering new user: {}", registerUserRequestDto);
        if (isPasswordLengthInvalid(registerUserRequestDto.getPassword())) {
            throw new InvalidPasswordException();
        }

        User user = userService.registerUser(registerUserRequestDto, registerUserRequestDto.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user
     *                          couldn't be activated.
     */

    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {

        Optional<User> user = userService.activateRegistration(key);

        if (!user.isPresent()) {
            throw new AccountControllerException("No user was found for this activation key");
        } else {

            mailService.sendCreationEmail(user.get());
        }

    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return
     * its userName.
     *
     * @param request the HTTP request.
     * @return the userName if the user is authenticated.
     */

    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");

        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user
     *                          couldn't be returned.
     */

    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService.getUserWithAuthorities().map(AdminUserDTO::new)
                .orElseThrow(() -> new AccountControllerException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is
     *                                   already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the
     *                                   user userName wasn't found.
     */

    @PutMapping("/account")
    public void updateAccount(@RequestBody @Valid AdminUserDTO userDTO) {

        String userName = SecurityUtils.getCurrentUserUserName()
                .orElseThrow(() -> new AccountControllerException("Current user userName not found"));

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());

        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(userName))) {
            throw new EmailAlreadyUsedException();
        }

        Optional<User> user = userRepository.findOneByUserName(userName);

        if (!user.isPresent()) {
            throw new AccountControllerException("User could not be found");
        }

        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getLangKey(),
                userDTO.getImageUrl());

    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeRequestDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new
     *                                  password is incorrect.
     */

    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto) {

        if (isPasswordLengthInvalid(passwordChangeRequestDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeRequestDto.getCurrentPassword(), passwordChangeRequestDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the
     * password of the user.
     *
     * @param mail the mail of the user.
     */

    @PostMapping(path = "/account/reset-password/init")
    public void requsetPasswordRest(@RequestBody String mail) {


        Optional<User> user = userService.requestPasswordReset(mail);

        if (user.isPresent()) {
            mailService.sendPasswordResetEmail(user.get());
        } else {

            // Pretend the request has been successful to prevent checking which emails
            // really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non-existing email: {}", mail);

        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password
     * of the user.
     *
     * @param keyAndPasswordVM the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is
     *                                  incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the
     *                                  password could not be reset.
     */

    @PostMapping(path = "/account/reset-password/finish")
    public void finshPasswordRest(@RequestBody ResetKeyAndPasswordRequestDto keyAndPasswordVM) {

        if (isPasswordLengthInvalid(keyAndPasswordVM.getNewPassword())) {
            throw new InvalidPasswordException();
        }

        Optional<User> user = userService.completePasswordReset(keyAndPasswordVM.getNewPassword(),
                keyAndPasswordVM.getKey());
        if (!user.isPresent()) {
            throw new AccountControllerException("No user was found for this reset key");
        }

    }


    /**
     * Upload or update the logged-in user's profile image.
     */
    @PostMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(@RequestParam("image") MultipartFile file) throws IOException {
        // 1) Extract username from Security Context
        Optional<String>  optionalUserName = SecurityUtils.getCurrentUserUserName();

        String userName = optionalUserName.orElseThrow(() ->
                new ResourceNotFoundException("Current user not found in security context"));

        // 2) Update the user's profile image
        String newFileName = profileImageUpdateService.updateUserProfileImage(userName, file);

        return ResponseEntity.ok("Profile image updated: " + newFileName);
    }

    /**
     * Retrieve the logged-in user's profile image.
     */
    @GetMapping("/profile-image")
    public ResponseEntity<byte[]> getProfileImage() throws IOException {
        // 1) Extract username from Security Context
        Optional<String>  optionalUserName = SecurityUtils.getCurrentUserUserName();

        String userName = optionalUserName.orElseThrow(() ->
                new ResourceNotFoundException("Current user not found in security context"));

        byte[] imageData = profileImageUpdateService.loadImage(userName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or detect extension

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageData);
    }





    private static boolean isPasswordLengthInvalid(String password) {

        return (StringUtils.isEmpty(password) || password.length() > RegisterUserRequestDto.PASSWORD_MAX_LENGTH
                || password.length() < RegisterUserRequestDto.PASSWORD_MIN_LENGTH);
    }
}