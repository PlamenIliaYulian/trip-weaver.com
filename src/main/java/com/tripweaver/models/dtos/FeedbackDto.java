package com.tripweaver.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public class FeedbackDto {
    @NotNull(message = "Rating cannot be empty.")
    @Min(value = 0, message = "Rating must be between 0 and 5.")
    @Max(value = 5, message = "Rating must be between 0 and 5.")
    private int rating;

    @Size(max = 200, message = "Content must contain between 1 and 200 symbols.")
    private String content;

    public FeedbackDto() {
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
