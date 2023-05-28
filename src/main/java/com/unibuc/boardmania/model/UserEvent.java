package com.unibuc.boardmania.model;

import com.unibuc.boardmania.enums.UserEventPlace;
import com.unibuc.boardmania.enums.UserEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_event")
@Entity(name = "user_event")
public class UserEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="event_id", nullable=false)
    private Event event;

    @Enumerated(EnumType.STRING)
    private UserEventPlace userEventPlace;

    private boolean sentConfirmationEmail;

    @Enumerated(EnumType.STRING)
    private UserEventStatus userEventStatus;

    private boolean deleted;

}
