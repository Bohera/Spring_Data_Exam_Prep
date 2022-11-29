package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DayOfWeekend;
import softuni.exam.models.entity.Forecast;
import softuni.exam.models.entity.dtos.ForecastImportDto;
import softuni.exam.models.entity.dtos.ForecastImportWrapperDto;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;

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
import java.util.stream.Collectors;

import static softuni.exam.util.Paths.FORECASTS_XML_PATH;

@Service
public class ForecastServiceImpl implements ForecastService {

    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;

    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository, Validator validator, ModelMapper modelMapper) {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(FORECASTS_XML_PATH);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(ForecastImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();


        ForecastImportWrapperDto forecastsDtos =
                (ForecastImportWrapperDto) unmarshaller.unmarshal(FORECASTS_XML_PATH.toFile());

        List<ForecastImportDto> forecasts = forecastsDtos.getForecasts();

        List<String> results = new ArrayList<>();

        for (ForecastImportDto forecastImportDto : forecasts) {
            Set<ConstraintViolation<ForecastImportDto>> errors =
                    this.validator.validate(forecastImportDto);

            if(errors.isEmpty()) {
                Optional<Forecast> optionalForecast = this.forecastRepository.findByCity_IdAndDayOfWeek(
                        forecastImportDto.getCity(),
                        forecastImportDto.getDayOfWeek());

                if(optionalForecast.isEmpty()) {
                    Forecast forecast = this.modelMapper.map(forecastImportDto, Forecast.class);

                    City city = this.cityRepository.getById(forecastImportDto.getCity());

                    forecast.setCity(city);

                    this.forecastRepository.save(forecast);

                    results.add(String.format("Successfully import forecast %s - %.2f",
                            forecast.getDayOfWeek(),
                            forecast.getMaxTemperature()));

                } else {
                    results.add("Invalid forecast");
                }
            } else {
                results.add("Invalid forecast");
            }
        }

        return String.join(System.lineSeparator(), results);
    }

    @Override
    public String exportForecasts() {

        Long populationLessThan = 150000L;

        List<Forecast> selectedForecasts = this.forecastRepository
                .findAllByDayOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(
                        DayOfWeekend.SUNDAY,
                        populationLessThan);

        return selectedForecasts
                .stream()
                .map(Forecast::toString)
                .collect(Collectors.joining("\n"));
    }
}
