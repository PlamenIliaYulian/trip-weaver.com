package com.tripweaver.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDtoCreate extends UserDto {
    @NotNull
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 symbols.")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
