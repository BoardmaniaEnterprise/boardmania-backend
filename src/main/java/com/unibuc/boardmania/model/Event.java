package com.unibuc.boardmania.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String location;

    private LocalDateTime dateTime;

    private boolean online;

    private int maxNumberOfPlayers;

    private Double minTrustScore;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    @JsonIgnoreProperties(value = "createdEvents")
    private User initiator;

    @OneToMany(mappedBy = "event")
    private List<EventGame> eventGames;

    @OneToMany(mappedBy = "event")
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game pickedGame;

    private boolean deleted;

}
