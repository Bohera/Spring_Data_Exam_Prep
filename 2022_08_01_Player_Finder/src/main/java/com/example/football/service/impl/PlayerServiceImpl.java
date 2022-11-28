package com.example.football.service.impl;

import com.example.football.models.dto.PlayerImportDto;
import com.example.football.models.dto.wrapper.PlayerImportWrapperDto;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.football.util.Paths.PLAYERS_XML_PATH;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    private final TownRepository townRepository;

    private final TeamRepository teamRepository;

    private final StatRepository statRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;


    public PlayerServiceImpl(
            PlayerRepository playerRepository,
            TownRepository townRepository,
            TeamRepository teamRepository,
            StatRepository statRepository,
            Validator validator,
            ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.townRepository = townRepository;
        this.teamRepository = teamRepository;
        this.statRepository = statRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(PLAYERS_XML_PATH);
    }

    @Override
    public String importPlayers() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PlayerImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();


        PlayerImportWrapperDto playersDtos =
                (PlayerImportWrapperDto) unmarshaller.unmarshal(PLAYERS_XML_PATH.toFile());


        List<PlayerImportDto> stats = playersDtos.getPlayers();

        List<String> result = new ArrayList<>();

        for (PlayerImportDto playerImportDto : stats) {
            Set<ConstraintViolation<PlayerImportDto>> errors = this.validator.validate(playerImportDto);

            if(errors.isEmpty()) {
                Optional<Player> optionalPlayer = this.playerRepository
                        .findByEmail(playerImportDto.getEmail());

                if(optionalPlayer.isEmpty()) {
                    Player player = this.modelMapper.map(playerImportDto, Player.class);

                    Optional<Town> town = this.townRepository.findByName(playerImportDto.getTown().getName());

                    Optional<Team> team = this.teamRepository.findByName(playerImportDto.getTeam().getName());

                    Optional<Stat> stat = this.statRepository.findById(playerImportDto.getStat().getId());

                    player.setTown(town.get());
                    player.setTeam(team.get());
                    player.setStat(stat.get());

                    this.playerRepository.save(player);

                    result.add(String.format("Successfully imported Player %s %s - %s",
                            player.getFirstName(),
                            player.getLastName(),
                            player.getPosition()));

                } else {
                    result.add("Invalid Player");
                }

            } else {
                result.add("Invalid Player");
            }
        }
        return String.join(System.lineSeparator(), result);
    }

    @Override
    public String exportBestPlayers() {
        LocalDate after = LocalDate.of(1995, 1, 1);
        LocalDate before = LocalDate.of(2003,1,1);

        List<Player> players = this.playerRepository.findByBirthDateBetweenOrderByStatShootingDescStatPassingDescStatEnduranceDescLastNameAsc(after, before);

        return players
                .stream()
                .map(Player::toString)
                .collect(Collectors.joining("\n"));
    }
}
