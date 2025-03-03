package com.filmbase.exception;

public class ImageFileSizeExceededException extends RuntimeException {
    public ImageFileSizeExceededException(String message) {
        super(message);
    }
}