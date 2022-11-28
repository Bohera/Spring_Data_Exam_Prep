package com.example.football.models.dto;

import com.example.football.models.entity.PlayerPosition;
import com.example.football.util.LocalDateAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerImportDto {

    @XmlElement(name = "first-name")
    @Size(min = 3)
    @NotNull
    private String firstName;

    @XmlElement(name = "last-name")
    @Size(min = 3)
    @NotNull
    private String lastName;

    @XmlElement
    @NotNull
    @Email(regexp = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,63})$")
    private String email;

    @XmlElement(name = "birth-date")
    @NotNull
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate birthDate;

    @XmlElement
    @NotNull
    private PlayerPosition position;

    @XmlElement(name = "stat")
    @NotNull
    private StatIdDto stat;

    @XmlElement(name = "team")
    @NotNull
    private TeamNameDto team;

    @XmlElement(name = "town")
    @NotNull
    private TownNameDto town;


}
