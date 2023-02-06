package com.unibuc.boardmania.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private int trustScore;

    @OneToMany(mappedBy = "initiator")
    private List<Event> createdEvents;

    @OneToMany(mappedBy = "user")
    private List<Rank> ranks;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewsSubmitted;

    @OneToMany(mappedBy = "reviewedUser")
    private List<Review> reviewsReceived;

    @OneToMany(mappedBy = "reporter")
    private List<Report> reportsSubmitted;

    @OneToMany(mappedBy = "reportedUser")
    private List<Report> reportsReceived;

    private boolean deleted;

    public User(String username, String firstName, String lastName, String email,
                int trustScore, List<Event> createdEvents, List<Rank> ranks,
                List<Review> reviewsSubmitted, List<Review> reviewsReceived,
                List<Report> reportsSubmitted, List<Report> reportsReceived, boolean deleted) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.trustScore = trustScore;
        this.createdEvents = createdEvents;
        this.ranks = ranks;
        this.reviewsSubmitted = reviewsSubmitted;
        this.reviewsReceived = reviewsReceived;
        this.reportsSubmitted = reportsSubmitted;
        this.reportsReceived = reportsReceived;
        this.deleted = deleted;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder
    {
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private int trustScore;
        private boolean deleted;
        private List<Event> createdEvents;
        private List<Rank> ranks;
        private List<Review> reviewsSubmitted;
        private List<Review> reviewsReceived;
        private List<Report> reportsSubmitted;
        private List<Report> reportsReceived;

        public UserBuilder() {
        }

        public UserBuilder username(final String username) {
            this.username = username;
            return this;
        }

        public UserBuilder firstName(final String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(final String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public UserBuilder trustScore(final int trustScore) {
            this.trustScore = trustScore;
            return this;
        }

        public UserBuilder deleted(final boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public UserBuilder createdEvents(final List<Event> createdEvents) {
            this.createdEvents = createdEvents;
            return this;
        }

        public UserBuilder ranks(final List<Rank> ranks) {
            this.ranks = ranks;
            return this;
        }

        public UserBuilder reviewsSubmitted(final List<Review> reviewsSubmitted) {
            this.reviewsSubmitted = reviewsSubmitted;
            return this;
        }

        public UserBuilder reviewsReceived(final List<Review> reviewsReceived) {
            this.reviewsReceived = reviewsReceived;
            return this;
        }

        public UserBuilder reportsSubmitted(final List<Report> reportsSubmitted) {
            this.reportsSubmitted = reportsSubmitted;
            return this;
        }

        public UserBuilder reportsReceived(final List<Report> reportsReceived) {
            this.reportsReceived = reportsReceived;
            return this;
        }

        public User build() {
            return new User(username, firstName, lastName, email, trustScore,
                    createdEvents, ranks, reviewsSubmitted, reviewsReceived,
                    reportsSubmitted, reportsReceived, deleted);
        }
    }

}
