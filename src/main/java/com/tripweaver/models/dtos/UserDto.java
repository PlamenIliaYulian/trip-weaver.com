package com.tripweaver.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {
    @NotNull
    @Size(min = 8, max = 25, message = "Password must be between 8 and 25 symbols.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, " +
                    "one number and one special character")
    private String password;

    @NotNull
    @Size(min = 8, max = 25, message = "Password must be between 8 and 25 symbols.")
    private String confirmPassword;

    @NotNull
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 symbols.")
    private String firstName;
    @NotNull
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 symbols.")
    private String lastName;
    @NotNull
    @Email
    @Pattern(
            regexp = "^(?!.*[?:{}])[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Email must be a valid email. It cannot contain '?', ':', '!', '}', '{'.")

    private String email;
    @NotNull
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits starting with 08.")
    @Pattern(regexp = "^08\\d{8}$", message = "Phone number must be 10 digits starting with 08.")
    private String phoneNumber;

    public UserDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
