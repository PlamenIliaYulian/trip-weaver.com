package com.tripweaver.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback_for_passengers_comments")
public class CommentForPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @JsonIgnore
    @Column(name = "feedback_id")
    private int feedbackForPassengerId;

    @Column(name = "content")
    private String content;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getFeedbackForPassengerId() {
        return feedbackForPassengerId;
    }

    public void setFeedbackForPassengerId(int feedbackForPassengerId) {
        this.feedbackForPassengerId = feedbackForPassengerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
