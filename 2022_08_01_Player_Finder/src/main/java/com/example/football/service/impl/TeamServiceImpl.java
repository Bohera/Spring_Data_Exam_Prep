package com.example.football.service.impl;

import com.example.football.models.dto.TeamImportDto;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.TeamService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.football.util.Paths.TEAMS_JSON_PATH;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    private final TownRepository townRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, ModelMapper modelMapper, Gson gson, Validator validator, TownRepository townRepository) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(TEAMS_JSON_PATH);
    }

    @Override
    public String importTeams() throws IOException {
        String jsonTeams = readTeamsFileContent();

        TeamImportDto[] teamImportDtos = this.gson.fromJson(jsonTeams, TeamImportDto[].class);

        List<String> result = new ArrayList<>();

        for (TeamImportDto teamImportDto : teamImportDtos) {
            Set<ConstraintViolation<TeamImportDto>> errors =
                    this.validator.validate(teamImportDto);

            if(errors.isEmpty()) {
                Optional<Team> optionalTeam = this.teamRepository.findByName(teamImportDto.getName());

                if(optionalTeam.isEmpty()) {
                    Team team = this.modelMapper.map(teamImportDto, Team.class);
                    Optional<Town> town = this.townRepository.findByName(teamImportDto.getTownName());

                    team.setTown(town.get());

                    this.teamRepository.save(team);

                    result.add(String.format("Successfully imported Town %s - %d",
                            team.getName(),
                            team.getFanBase()));
                } else {
                    result.add("Invalid Team");
                }
            } else {
                result.add("Invalid Team");
            }
        }

        return String.join(System.lineSeparator(), result);
    }
}
