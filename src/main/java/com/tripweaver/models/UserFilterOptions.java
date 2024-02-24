package com.tripweaver.models;

import java.util.Optional;

public class UserFilterOptions {

    private final Optional<String> username;
    private final Optional<String> email;
    private final Optional<String> firstName;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public UserFilterOptions() {
        this.username = Optional.empty();
        this.email = Optional.empty();
        this.firstName = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
    }

    public UserFilterOptions(String username, String email, String firstName, String sortBy, String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.firstName = Optional.ofNullable(firstName);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
