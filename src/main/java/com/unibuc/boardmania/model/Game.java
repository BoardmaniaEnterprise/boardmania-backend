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

    @ManyToMany(mappedBy = "eventGames")
    private List<Event> gameEvents;

    private boolean deleted;

}
