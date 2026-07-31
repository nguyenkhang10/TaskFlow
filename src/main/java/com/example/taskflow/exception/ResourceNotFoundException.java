package com.example.taskflow.exception;

/**
 * Ném ra khi thao tác trên một project không tồn tại.
 * GlobalExceptionHandler sẽ bắt exception này và trả HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
