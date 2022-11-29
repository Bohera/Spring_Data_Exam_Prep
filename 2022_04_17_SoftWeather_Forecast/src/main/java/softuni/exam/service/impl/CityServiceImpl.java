package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.models.entity.dtos.CityImportDto;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.CITIES_JSON_PATH;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    public CityServiceImpl(CityRepository cityRepository, CountryRepository countryRepository, ModelMapper modelMapper, Gson gson, Validator validator) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(CITIES_JSON_PATH);
    }

    @Override
    public String importCities() throws IOException {
        String jsonCities = readCitiesFileContent();

        CityImportDto[] cityImportDtos =
                this.gson.fromJson(jsonCities, CityImportDto[].class);

        List<String> results = new ArrayList<>();

        for (CityImportDto cityImportDto : cityImportDtos) {
            Set<ConstraintViolation<CityImportDto>> errors =
                    this.validator.validate(cityImportDto);

            if(errors.isEmpty()) {
                Optional<City> optionalCity = this.cityRepository.findByCityName(cityImportDto.getCityName());

                if(optionalCity.isEmpty()) {
                    City city = this.modelMapper.map(cityImportDto, City.class);

                    Country country = this.countryRepository.getById(cityImportDto.getCountry());

                    city.setCountry(country);

                    this.cityRepository.save(city);

                    results.add(String.format("Successfully imported city %s - %s",
                            city.getCityName(),
                            city.getPopulation()));

                } else {
                    results.add("Invalid city");
                }

            } else {
                results.add("Invalid city");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
