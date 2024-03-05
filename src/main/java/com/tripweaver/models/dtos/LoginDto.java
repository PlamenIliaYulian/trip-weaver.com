package com.tripweaver.models.dtos;

import jakarta.validation.constraints.NotEmpty;

public class LoginDto {
    @NotEmpty(message = "Username cannot be empty.")
    String username;
    @NotEmpty(message = "Password cannot be empty.")
    String password;

    public LoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
