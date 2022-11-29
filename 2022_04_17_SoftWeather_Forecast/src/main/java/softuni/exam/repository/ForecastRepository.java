package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.DayOfWeekend;
import softuni.exam.models.entity.Forecast;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    Optional<Forecast> findByCity_IdAndDayOfWeek(Long id, DayOfWeekend day);

    List<Forecast> findAllByDayOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(DayOfWeekend day, Long populationLessThan);

}
