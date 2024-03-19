package com.tripweaver.models.enums;

public enum EmailVerificationType {
    REST ("/api/v1/auth"),
    MVC("/auth");

    private final String text;

    EmailVerificationType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
