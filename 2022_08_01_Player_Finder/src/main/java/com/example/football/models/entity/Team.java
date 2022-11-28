package com.example.football.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teams")
public class Team extends BaseEntity{

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "stadium_name", nullable = false)
    private String stadiumName;

    @Column(name = "fan_base", nullable = false)
    private int fanBase;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String history;

    @ManyToOne(optional = false)
    private Town town;

    @OneToMany(targetEntity = Player.class, mappedBy = "team")
    private Set<Player> players;

}
