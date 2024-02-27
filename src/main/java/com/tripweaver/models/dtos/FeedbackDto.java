package com.tripweaver.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackDto {
    @NotNull(message = "Receiver ID cannot be empty.")
    private int receiverUserId;

    @NotNull(message = "Rating cannot be empty.")
    @Size(min = 0, max = 5, message = "Rating must be between 1 and 5.")
    private int rating;

    @Size(max = 200, message = "Content must contain between 1 and 200 symbols.")
    private String content;

    public FeedbackDto() {
    }

    public int getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(int receiverUserId) {
        this.receiverUserId = receiverUserId;
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
