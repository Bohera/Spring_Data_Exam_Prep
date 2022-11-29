package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.entity.Country;
import softuni.exam.models.entity.dtos.CountryImportDto;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.COUNTRIES_JSON_PATH;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, ModelMapper modelMapper, Gson gson, Validator validator) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(COUNTRIES_JSON_PATH);
    }

    @Override
    public String importCountries() throws IOException {
        String jsonCountries = readCountriesFromFile();

        CountryImportDto[] countryImportDtos =
                this.gson.fromJson(jsonCountries, CountryImportDto[].class);

        List<String> results = new ArrayList<>();

        for (CountryImportDto countryImportDto : countryImportDtos) {
            Set<ConstraintViolation<CountryImportDto>> errors =
                    this.validator.validate(countryImportDto);

            if(errors.isEmpty()) {
                Optional<Country> optionalCountry = this.countryRepository.findByCountryName(countryImportDto.getCountryName());

                if(optionalCountry.isEmpty()) {
                    Country country = this.modelMapper.map(countryImportDto, Country.class);

                    this.countryRepository.save(country);

                    results.add(String.format("Successfully imported country %s - %s",
                            country.getCountryName(),
                            country.getCurrency()));

                } else {
                    results.add("Invalid country");
                }

            } else {
                results.add("Invalid country");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
