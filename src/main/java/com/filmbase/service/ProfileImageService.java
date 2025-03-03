package com.filmbase.service;

import com.filmbase.exception.ImageFileSizeExceededException;
import com.filmbase.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ProfileImageService {

    private final Path rootLocation;
    private final long MAX_FILE_SIZE = 10L * 1024L * 1024L; // 10 MB

    public ProfileImageService(@Value("${profile.image.location}") String location) {
        this.rootLocation = Paths.get(location);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for profile images", e);
        }
    }

    /**
     * Stores the file on disk as {userName}.{extension}, limited to 10 MB.
     * Overwrites if a file with the same name exists.
     */
    public String storeImage(MultipartFile image, String userName) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new ResourceNotFoundException("image is empty");
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ImageFileSizeExceededException("File is bigger than 10 MB");
        }

        String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());

        String baseFilename = Paths.get(originalFilename).getFileName().toString();

        String sanitizedFilename = baseFilename.replaceAll("\\s+", "_");

        sanitizedFilename = sanitizedFilename.replaceAll("[^a-zA-Z0-9._-]", "");
         sanitizedFilename = originalFilename.replace("..", "");
        sanitizedFilename = sanitizedFilename.replaceAll("[/\\\\]", "");
        String extension = getFileExtension(originalFilename);


        String fileName = userName+"_" +sanitizedFilename + (extension.isEmpty() ? "" : "." + extension);

        Path destinationFile = this.rootLocation.resolve(fileName).normalize();
        Files.copy(image.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * Loads the file from disk by searching for {userName}.*
     */
    public byte[] loadImage(String userName) throws IOException {
        File folder = this.rootLocation.toFile();
        File[] matchingFiles = folder.listFiles((dir, name) -> name.startsWith(userName + "_"));

        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new ResourceNotFoundException("Profile image not found for userName: " + userName);
        }

        Path filePath = matchingFiles[0].toPath();
        return Files.readAllBytes(filePath);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }
}
