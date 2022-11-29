package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "countries")
public class Country extends BaseEntity{

    @Column(name = "country_name", unique = true, nullable = false)
    private String countryName;

    @Column(nullable = false)
    private String currency;

    @OneToMany(targetEntity = City.class, mappedBy = "country")
    private List<City> cities;
}
