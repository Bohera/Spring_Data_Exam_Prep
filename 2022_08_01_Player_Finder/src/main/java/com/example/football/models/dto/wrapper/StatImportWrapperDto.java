package com.example.football.models.dto.wrapper;

import com.example.football.models.dto.StatImportDto;
import com.example.football.models.entity.Stat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "stats")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatImportWrapperDto {

    @XmlElement(name = "stat")
    private List<StatImportDto> stats;

}
