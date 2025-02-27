package com.filmbase.service;

import com.filmbase.exception.EmptyFileException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FileService interface providing file management functionality.
 */

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File cannot be empty");
        }

        // Create directory if it doesn't exist
        createDirectory(path);

        // Generate unique filename
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = normalizePath(path + File.separator + fileName);
        // Log the path for debugging
        log.info("File will be stored at: " + filePath);
        // Copy file to destination
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(filePath));
        }

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {

        if (path == null || path.trim().isEmpty() || fileName == null || fileName.trim().isEmpty()) {
            throw new EmptyFileException("Path and file name must not be empty");
        }

        String fullPath = path + File.separator + fileName;
        File file = new File(fullPath);

        if (!file.exists()) {
            log.error("File not found: " + fullPath);
            throw new EmptyFileException("File not found: " + fileName);
        }

        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new EmptyFileException("Error accessing the file: " + file.toString());
        }
    }

    @Override
    public boolean isValidFileType(MultipartFile file, List<String> allowedExtensions) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return false;
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        return allowedExtensions.contains(extension.toLowerCase());
    }

    @Override
    public boolean deleteFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.deleteIfExists(filePath);
    }

    @Override
    public boolean createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            return true;
        }
        return false;
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        return sanitizeFileName(originalFilename);
    }


    @Override
    public String getReadableFileSize(MultipartFile file) {
        long size = file.getSize();
        if (size <= 0) return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        DecimalFormat df = new DecimalFormat("#,##0.#");
        return df.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }


    @Override
    public List<String> listFiles(String directoryPath) throws IOException {
        try (var paths = Files.walk(Paths.get(directoryPath), 1)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Helper method to normalize file path
     *
     * @param path the path to normalize
     * @return normalized path string
     */
    private String normalizePath(String path) {
        return Paths.get(path).normalize().toString();
    }

    /**
     * Helper method to ensure file name safety
     *
     * @param fileName the file name to sanitize
     * @return sanitized file name
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }


}