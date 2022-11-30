package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AgentImportDto;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.AgentService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.AGENTS_JSON_PATH;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    private final TownRepository townRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    @Autowired
    public AgentServiceImpl(
            AgentRepository agentRepository,
            TownRepository townRepository, ModelMapper modelMapper,
            Gson gson,
            Validator validator) {
        this.agentRepository = agentRepository;
        this.townRepository = townRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }

    @Override
    public boolean areImported() {
        return this.agentRepository.count() > 0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return Files.readString(AGENTS_JSON_PATH);
    }

    @Override
    public String importAgents() throws IOException {
        String jsonAgents = readAgentsFromFile();

        AgentImportDto[] agentImportDtos =
                this.gson.fromJson(jsonAgents, AgentImportDto[].class);

        List<String> results = new ArrayList<>();

        for (AgentImportDto agentImportDto : agentImportDtos) {
            Set<ConstraintViolation<AgentImportDto>> errors =
                    this.validator.validate(agentImportDto);

            if(errors.isEmpty()) {
                Optional<Agent> optionalAgent =
                        this.agentRepository.findByFirstName(agentImportDto.getFirstName());

                if(optionalAgent.isEmpty()) {
                    Agent agent = this.modelMapper.map(agentImportDto, Agent.class);

                    Optional<Town> town =
                            this.townRepository.findByTownName(agentImportDto.getTown());

                    agent.setTown(town.get());

                    this.agentRepository.save(agent);

                    results.add(String.format("Successfully imported agent - %s %s",
                            agent.getFirstName(),
                            agent.getLastName()));

                } else {
                    results.add("Invalid agent");
                }
            } else {
                results.add("Invalid agent");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
