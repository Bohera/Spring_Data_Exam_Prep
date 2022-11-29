package softuni.exam.models.entity.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.Country;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityImportDto {

    @Size(min = 2, max = 60)
    private String cityName;

    @Size(min = 2)
    private String description;

    @Min(500)
    private long population;


    private Long country;
}
