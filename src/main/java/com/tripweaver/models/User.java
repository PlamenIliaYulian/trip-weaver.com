package com.tripweaver.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Comparable<User> {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name = "is_verified")
    private boolean isVerified;
    @Column(name = "is_blocked")
    private boolean isBlocked;
    @Column(name = "average_passenger_rating")
    private int averagePassengerRating;
    @Column(name = "average_driver_rating")
    private int averageDriverRating;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "avatars_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "avatar_id"))
    private Avatar avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "driverReceivedFeedback", fetch = FetchType.EAGER)
    private Set<FeedbackForDriver> feedbackForDriver;
    @JsonIgnore
    @OneToMany(mappedBy = "passengerReceivedFeedback", fetch = FetchType.EAGER)
    private Set<FeedbackForPassenger> feedbackForPassenger;


    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public int getAveragePassengerRating() {
        return averagePassengerRating;
    }

    public void setAveragePassengerRating(int averagePassengerRating) {
        this.averagePassengerRating = averagePassengerRating;
    }

    public int getAverageDriverRating() {
        return averageDriverRating;
    }

    public void setAverageDriverRating(int averageDriverRating) {
        this.averageDriverRating = averageDriverRating;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<FeedbackForDriver> getFeedbackForDriver() {
        return feedbackForDriver;
    }

    public void setFeedbackForDriver(Set<FeedbackForDriver> feedbackForDriver) {
        this.feedbackForDriver = feedbackForDriver;
    }

    public Set<FeedbackForPassenger> getFeedbackForPassenger() {
        return feedbackForPassenger;
    }

    public void setFeedbackForPassenger(Set<FeedbackForPassenger> feedbackForPassenger) {
        this.feedbackForPassenger = feedbackForPassenger;
    }

    @Override
    public int compareTo(User o) {
        return Integer.compare(this.getUserId(), o.getUserId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
