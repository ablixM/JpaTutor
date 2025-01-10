package com.binarydreamers.springboot.labjpa.DTO;

public class LoginRequest {
    private String email; // or username, depending on your application
    private String password;
    private String token;

    public LoginRequest(String email, String token) {
        this.email = email;
        this.token = token;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}