package com.tripweaver.models.filterOptions;

import java.util.Optional;

public class UserFilterOptions {

    private final Optional<String> username;
    private final Optional<String> email;
    private final Optional<String> phoneNumber;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public UserFilterOptions(String username, String email,
                             String phoneNumber, String sortBy,
                             String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
