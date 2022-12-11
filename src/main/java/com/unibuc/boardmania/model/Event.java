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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String location;

    private boolean online;

    private int maxNumberOfPlayers;

    private Double minTrustScore;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User initiatior;

    @ManyToMany
    @JoinTable(name = "event_games",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Game> eventGames;

    private boolean deleted;

}
