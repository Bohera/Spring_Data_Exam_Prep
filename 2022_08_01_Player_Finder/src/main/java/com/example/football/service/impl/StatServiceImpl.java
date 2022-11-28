package com.example.football.service.impl;

import com.example.football.models.dto.StatImportDto;
import com.example.football.models.dto.wrapper.StatImportWrapperDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.football.util.Paths.STATS_XML_PATH;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;

    @Autowired
    public StatServiceImpl(StatRepository statRepository, Validator validator, ModelMapper modelMapper) {
        this.statRepository = statRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(STATS_XML_PATH);
    }

    @Override
    public String importStats() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(StatImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        StatImportWrapperDto statsDtos =
                (StatImportWrapperDto) unmarshaller.unmarshal(STATS_XML_PATH.toFile());

        List<StatImportDto> stats = statsDtos.getStats();

        List<String> result = new ArrayList<>();

        for (StatImportDto statImportDto : stats) {
            Set<ConstraintViolation<StatImportDto>> errors = this.validator.validate(statImportDto);

            if(errors.isEmpty()) {
                Optional<Stat> optionalStat =
                        this.statRepository.findStatByPassingAndShootingAndEndurance(
                                statImportDto.getPassing(),
                                statImportDto.getShooting(),
                                statImportDto.getEndurance()
                        );

                if(optionalStat.isEmpty()) {
                    Stat stat = this.modelMapper.map(statImportDto, Stat.class);

                    this.statRepository.save(stat);

                    result.add(String.format("Successfully imported Stat %.2f - %.2f - %.2f",
                            stat.getPassing(),
                            stat.getShooting(),
                            stat.getEndurance()));
                } else {
                    result.add("Invalid Stat");
                }
            } else {
                result.add("Invalid Stat");
            }
        }
        return String.join(System.lineSeparator(), result);
    }
}
