package com.tripweaver.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "feedback_for_passengers")
@SecondaryTable(name = "feedback_for_passengers_comments", pkJoinColumns = @PrimaryKeyJoinColumn(name = "feedback_id"))
public class FeedbackForPassenger implements Comparable<FeedbackForPassenger>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int feedbackId;

    /*ToDo from Ilia - I'm not sure if this is the right approach to get User
     *  objects from the table using passenger_id and driver_id.*/
    @JoinColumn(name = "driver_id")
    @ManyToOne(targetEntity=User.class,fetch=FetchType.EAGER)
    private User driverProvidedFeedback;

    @JoinColumn(name = "passenger_id")
    @ManyToOne(targetEntity=User.class,fetch=FetchType.EAGER)
    private User passengerReceivedFeedback;

    @Column(name = "rating")
    private int rating;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "content", table = "feedback_for_passengers_comments")
    private String content;

    public FeedbackForPassenger() {
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public User getDriverProvidedFeedback() {
        return driverProvidedFeedback;
    }

    public void setDriverProvidedFeedback(User driverProvidedFeedback) {
        this.driverProvidedFeedback = driverProvidedFeedback;
    }

    public User getPassengerReceivedFeedback() {
        return passengerReceivedFeedback;
    }

    public void setPassengerReceivedFeedback(User passengerReceivedFeedback) {
        this.passengerReceivedFeedback = passengerReceivedFeedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public void setContent(String comment) {
        this.content = comment;
    }

    @Override
    public int compareTo(FeedbackForPassenger o) {
        return Integer.compare(this.feedbackId, o.feedbackId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackForPassenger that = (FeedbackForPassenger) o;
        return feedbackId == that.feedbackId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }
}
