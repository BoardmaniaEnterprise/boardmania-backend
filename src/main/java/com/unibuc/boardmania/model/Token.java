package com.unibuc.boardmania.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
@Entity(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    @Column(name = "`value`")
    private UUID value;

    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private Long relatedObjectId;

    @NotNull
    private Long creationTime;

    private Long expireTime;

}
