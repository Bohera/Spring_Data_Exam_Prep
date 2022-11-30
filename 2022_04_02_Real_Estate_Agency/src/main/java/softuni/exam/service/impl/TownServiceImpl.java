package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TownImportDto;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.TOWNS_JSON_PATH;

@Service
public class TownServiceImpl implements TownService {

    private final TownRepository townRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    @Autowired
    public TownServiceImpl(
            TownRepository townRepository,
            ModelMapper modelMapper,
            Gson gson,
            Validator validator) {
        this.townRepository = townRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }


    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(TOWNS_JSON_PATH);
    }

    @Override
    public String importTowns() throws IOException {
        String jsonTowns = readTownsFileContent();

        TownImportDto[] townImportDtos = this.gson.fromJson(jsonTowns, TownImportDto[].class);

        List<String> results = new ArrayList<>();

        for (TownImportDto townImportDto : townImportDtos) {
            Set<ConstraintViolation<TownImportDto>> errors =
                    this.validator.validate(townImportDto);

            if(errors.isEmpty()) {
                Optional<Town> optionalTown =
                        this.townRepository.findByTownName(townImportDto.getTownName());

                if(optionalTown.isEmpty()) {
                    Town town = this.modelMapper.map(townImportDto, Town.class);

                    this.townRepository.save(town);

                    results.add(String.format("Successfully imported town %s - %d",
                            town.getTownName(),
                            town.getPopulation()));

                } else {
                    results.add("Invalid town");
                }
            } else {
                results.add("Invalid town");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
