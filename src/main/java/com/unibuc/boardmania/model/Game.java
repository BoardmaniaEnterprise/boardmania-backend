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
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private int minNumberOfPlayers;

    private int maxNumberOfPlayers;

    private String description;

    private String url;

    @OneToMany(mappedBy = "game")
    private List<EventGame> eventGames;

    @OneToMany(mappedBy = "game")
    private List<Rank> ranks;

    @OneToMany(mappedBy = "pickedGame")
    private List<Event> events;

    private boolean deleted;

}
