package com.project.tour.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message)    {
        super(message);
    }
}