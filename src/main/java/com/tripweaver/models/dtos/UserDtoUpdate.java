package com.tripweaver.models.dtos;

import jakarta.validation.constraints.NotNull;

public class UserDtoUpdate extends UserDto{
    @NotNull
    private String confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
