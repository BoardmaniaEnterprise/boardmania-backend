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

}
