package com.binarydreamers.springboot.labjpa.Model;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}