package com.filmbase.controller;


import com.filmbase.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file/")
@RequiredArgsConstructor
public class FileController {


    private final FileService fileService;


    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.allowed.extensions}")
    private String allowedExtensions;

    @Value("${file.max.size:524288000}") // 500MB default for video support
    private long maxFileSize;

    /**
     * Upload a single file
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            List<String> allowedExtList = Arrays.asList(allowedExtensions.split(","));

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(createResponse("error", "File is empty"));
            }

            // Check file size
            if (file.getSize() > maxFileSize) {
                return ResponseEntity
                        .badRequest()
                        .body(createResponse("error",
                                "File size exceeds limit of " + fileService.getReadableFileSize(file)));
            }

            // Validate file type
            if (!fileService.isValidFileType(file, allowedExtList)) {
                return ResponseEntity
                        .badRequest()
                        .body(createResponse("error",
                                "Invalid file type. Allowed types: " + allowedExtensions));
            }

            String fileName = fileService.uploadFile(uploadPath, file);

            return ResponseEntity
                    .ok()
                    .body(createResponse("fileName", fileName));

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponse("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download a file
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            InputStream resource = fileService.getResourceFile(uploadPath, fileName);
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(resource));

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a file
     */
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String fileName) {
        try {
            String filePath = uploadPath + File.separator + fileName;

            if (!fileService.fileExists(filePath)) {
                return ResponseEntity
                        .notFound()
                        .build();
            }

            boolean deleted = fileService.deleteFile(filePath);

            if (deleted) {
                return ResponseEntity
                        .ok()
                        .body(createResponse("message", "File deleted successfully"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponse("error", "Failed to delete file"));
            }

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponse("error", "Error deleting file: " + e.getMessage()));
        }
    }

    /**
     * List all files in upload directory
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        try {
            List<String> files = fileService.listFiles(uploadPath);
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Check if file exists
     */
    @GetMapping("/exists/{fileName}")
    public ResponseEntity<Map<String, Boolean>> checkFileExists(@PathVariable String fileName) {
        String filePath = uploadPath + File.separator + fileName;
        boolean exists = fileService.fileExists(filePath);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    private Map<String, String> createResponse(String key, String value) {
        Map<String, String> response = new HashMap<>();
        response.put(key, value);
        return response;
    }

    private String determineContentType(String fileName) {
        String ext = fileService.getFileExtension(fileName).toLowerCase();
        switch (ext) {
            // Existing types
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            // Added video types
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "wmv":
                return "video/x-ms-wmv";
            case "mov":
                return "video/quicktime";
            case "mkv":
                return "video/x-matroska";
            case "flv":
                return "video/x-flv";
            case "webm":
                return "video/webm";
            case "3gp":
                return "video/3gpp";
            default:
                return "application/octet-stream";
        }
    }


}
