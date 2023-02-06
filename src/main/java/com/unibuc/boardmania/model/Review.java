package com.unibuc.boardmania.model;

import com.unibuc.boardmania.enums.HonorTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;

    private HonorTitle honor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User reviewedUser;

    @ManyToOne
    @JoinColumn(name = "reviwer_id", nullable = false)
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private boolean deleted;
}
