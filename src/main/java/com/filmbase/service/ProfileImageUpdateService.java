package com.filmbase.service;


import com.filmbase.entity.User;
import com.filmbase.exception.ResourceNotFoundException;
import com.filmbase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileImageUpdateService {

    private final ProfileImageService profileImageService;
    private final UserRepository userRepository;

    /**
     * Finds the user by userName, stores the file, and updates user.imageUrl
     */

    public String updateUserProfileImage(String userName, MultipartFile file) throws IOException {
        // 1) Retrieve the user from DB by userName
        User user = userRepository.findOneByUserName(userName.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userName));

        // 2) Store the file on disk
        String storedFileName = profileImageService.storeImage(file, userName);

        // 3) Update the imageUrl in the user entity
        user.setImageUrl(storedFileName);

        // 4) Save user
        userRepository.save(user);

        return storedFileName;
    }

    public byte[] loadImage(String userName) throws IOException {

        return profileImageService.loadImage(userName);
    }

}
