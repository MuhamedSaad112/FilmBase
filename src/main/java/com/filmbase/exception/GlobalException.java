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

    // Existing handlers
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


    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestAlertException(BadRequestAlertException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(ImageFileSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleImageFileSizeExceededException(ImageFileSizeExceededException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, ex, request);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(UserNameAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleUserNameAlreadyUsedException(UserNameAlreadyUsedException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotActivatedException(UserNotActivatedException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex, request);
    }

    // Catch-all for any other exceptions not explicitly handled
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    // Helper method to build the response body
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

    // Customize Arabic messages per exception
    private String getMessageInArabic(Exception ex) {
        if (ex instanceof MovieNotFoundException) {
            return "الفيلم غير موجود. حاول مرة أخرى.";
        } else if (ex instanceof FileExistsException) {
            return "الملف موجود بالفعل. يرجى اختيار ملف آخر.";
        } else if (ex instanceof EmptyFileException) {
            return "الملف فارغ. يرجى اختيار ملف صالح.";
        } else if (ex instanceof BadRequestAlertException) {
            return "طلب غير صالح. يرجى التحقق من البيانات المدخلة.";
        } else if (ex instanceof EmailAlreadyUsedException) {
            return "البريد الإلكتروني مستخدم بالفعل. يرجى استخدام بريد آخر.";
        } else if (ex instanceof ImageFileSizeExceededException) {
            return "حجم ملف الصورة أكبر من الحد المسموح به.";
        } else if (ex instanceof InvalidPasswordException) {
            return "كلمة المرور غير صحيحة.";
        } else if (ex instanceof ResourceNotFoundException) {
            return "المورد المطلوب غير موجود.";
        } else if (ex instanceof UserNameAlreadyUsedException) {
            return "اسم المستخدم مستخدم بالفعل.";
        } else if (ex instanceof UserNotActivatedException) {
            return "الحساب غير مُفعل. يرجى تفعيل الحساب أو التواصل مع المسؤول.";
        } else {
            return "حدث خطأ غير متوقع، يرجى التواصل مع المسؤول.";
        }
    }
}
