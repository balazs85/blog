package com.example.blog.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> entityClass, Object id) {
        super(entityClass.getSimpleName() + " not found: " + id);
    }
}
