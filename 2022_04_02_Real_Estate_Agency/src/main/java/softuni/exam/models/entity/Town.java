package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "towns")
public class Town extends BaseEntity{

    @Column(name = "town_name", unique = true, nullable = false)
    private String townName;

    @Column(nullable = false)
    private int population;

    @OneToMany(targetEntity = Agent.class, mappedBy = "town")
    private List<Agent> agents;

    @OneToMany(targetEntity = Apartment.class, mappedBy = "town")
    private List<Apartment> apartments;

}
