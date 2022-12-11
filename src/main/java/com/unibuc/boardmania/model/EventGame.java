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
@Table(name = "event_game")
public class EventGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="event_id", nullable=false)
    private Event event;

    @ManyToOne
    @JoinColumn(name="game_id", nullable=false)
    private Game game;

    @OneToMany
    @JoinColumn(name = "vote_id", nullable = false)
    private List<Vote> votes;

    private String minRank;

    private String maxRank;

    private boolean deleted;
}
