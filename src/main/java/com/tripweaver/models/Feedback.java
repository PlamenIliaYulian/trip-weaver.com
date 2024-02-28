package com.tripweaver.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tripweaver.models.enums.FeedbackType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@SecondaryTable(name = "comments_for_feedback", pkJoinColumns = @PrimaryKeyJoinColumn(name = "feedback_id"))
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int feedbackId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_author_id")
    private User author;

    @JsonIgnore
    @ManyToOne(targetEntity=User.class,fetch=FetchType.EAGER)
    @JoinColumn(name = "user_receiver_id")
    private User receiver;
    @Column(name = "rating")
    private int rating;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FeedbackType feedbackType;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "content", table = "comments_for_feedback")
    private String content;

    public Feedback() {
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
