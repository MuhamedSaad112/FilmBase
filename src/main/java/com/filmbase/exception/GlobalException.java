package com.filmbase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMovieNotFoundException(MovieNotFoundException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(FileExistsException.class)
    public ResponseEntity<Map<String, Object>> handleFileExistsException(FileExistsException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyFileException(EmptyFileException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, Exception ex, WebRequest request) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("timestamp", new Date());

        String messageEn = ex.getMessage();
        String messageAr = getMessageInArabic(ex);

        errorResponse.put("messageAr", messageAr);
        errorResponse.put("messageEn", messageEn);
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        errorResponse.put("details", messageEn);

        return new ResponseEntity<>(errorResponse, status);
    }

    private String getMessageInArabic(Exception ex) {
        if (ex instanceof MovieNotFoundException) {
            return "الفيلم غير موجود.حاول مره اخره";
        } else if (ex instanceof FileExistsException) {
            return "الملف موجود بالفعل.ادخل ملف اخر ";
        } else if (ex instanceof EmptyFileException) {
            return "الملف فارغ.ادخل الملف ";
        } else {
            return "حدث خطأ غير متوقع، يرجى التواصل مع المسؤول.";
        }
    }
}
