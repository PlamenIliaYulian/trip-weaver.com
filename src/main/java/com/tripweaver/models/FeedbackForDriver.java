package com.tripweaver.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "feedback_for_drivers")
@SecondaryTable(name = "feedback_for_drivers_comments", pkJoinColumns = @PrimaryKeyJoinColumn(name = "feedback_id"))
public class FeedbackForDriver implements Comparable<FeedbackForDriver>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int feedbackId;

    /*ToDo from Ilia - I'm not sure if this is the right approach to get User
    *  objects from the table using passenger_id and driver_id.*/
    @JoinColumn(name = "passenger_id")
    @ManyToOne(targetEntity=User.class,fetch=FetchType.EAGER)
    private User passengerProvidedFeedback;


    @JoinColumn(name = "driver_id")
    @ManyToOne(targetEntity=User.class,fetch=FetchType.EAGER)
    private User driverReceivedFeedback;


    @Column(name = "rating")
    private int rating;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "content", table = "feedback_for_drivers_comments")
    private String content;

    public FeedbackForDriver() {
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public User getPassengerProvidedFeedback() {
        return passengerProvidedFeedback;
    }

    public void setPassengerProvidedFeedback(User passengerProvidedFeedback) {
        this.passengerProvidedFeedback = passengerProvidedFeedback;
    }

    public User getDriverReceivedFeedback() {
        return driverReceivedFeedback;
    }

    public void setDriverReceivedFeedback(User driverReceivedFeedback) {
        this.driverReceivedFeedback = driverReceivedFeedback;
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
    public int compareTo(FeedbackForDriver o) {
        return Integer.compare(this.feedbackId, o.feedbackId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackForDriver that = (FeedbackForDriver) o;
        return feedbackId == that.feedbackId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }
}
