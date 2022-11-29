package softuni.exam.models.entity.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.DayOfWeekend;
import softuni.exam.util.LocalTimeAdapter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "forecast")
@XmlAccessorType(XmlAccessType.FIELD)
public class ForecastImportDto {

    @XmlElement(name = "day_of_week")
    @NotNull
    private DayOfWeekend dayOfWeek;

    @Min(-20)
    @Max(60)
    @XmlElement(name = "max_temperature")
    private double maxTemperature;

    @Min(-50)
    @Max(40)
    @XmlElement(name = "min_temperature")
    private double minTemperature;

    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime sunrise;

    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime sunset;

    @XmlElement
    private Long city;

}
