package com.filmbase.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Service interface for handling file operations including uploads, downloads,
 * and file management functionality.
 */
public interface FileService {

    /**
     * Uploads a file to the specified path
     * @param path destination directory path
     * @param file the file to upload
     * @return the generated filename with path
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file is empty or invalid
     */
    String uploadFile(String path, MultipartFile file) throws IOException;

    /**
     * Retrieves a file as an InputStream
     * @param path directory path of the file
     * @param name name of the file
     * @return InputStream of the file
     * @throws FileNotFoundException if file doesn't exist
     */
    InputStream getResourceFile(String path, String name) throws FileNotFoundException;

    /**
     * Validates if the file type is allowed
     *
     * @param file              file to validate
     * @param allowedExtensions list of allowed file extensions
     * @return true if file type is allowed, false otherwise
     */
    boolean isValidFileType(MultipartFile file, List<String> allowedExtensions);

    /**
     * Deletes a file from the specified path
     *
     * @param path full path of the file to delete
     * @return true if deletion was successful, false otherwise
     * @throws IOException if deletion operation fails
     */
    boolean deleteFile(String path) throws IOException;

    /**
     * Creates a directory if it doesn't exist
     *
     * @param path directory path to create
     * @return true if directory was created or already exists
     * @throws IOException if directory creation fails
     */
    boolean createDirectory(String path) throws IOException;

    /**
     * Generates a unique filename to avoid duplicates
     *
     * @param originalFilename original name of the file
     * @return unique filename
     */
    String generateUniqueFileName(String originalFilename);


    /**
     * Returns the file size in a human-readable format
     *
     * @param file the file to check
     * @return formatted string of file size (e.g., "1.5 MB")
     */
    String getReadableFileSize(MultipartFile file);

    /**
     * Checks if a file exists at the specified path
     *
     * @param path file path to check
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String path);

    /**
     * Lists all files in a directory
     *
     * @param directoryPath path to directory
     * @return list of filenames in the directory
     * @throws IOException if directory reading fails
     */
    List<String> listFiles(String directoryPath) throws IOException;

    /**
     * Gets the file extension from a filename
     *
     * @param filename name of the file
     * @return file extension or empty string if no extension
     */
    String getFileExtension(String filename);
}