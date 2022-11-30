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
@Table(name = "apartments")
public class Apartment extends BaseEntity{

    @Column(name = "apartment_type", columnDefinition = "VARCHAR(255)", nullable = false)
    private ApartmentType apartmentType;

    @Column(nullable = false)
    private double area;

    @ManyToOne
    private Town town;

    @OneToMany(targetEntity = Offer.class, mappedBy = "apartment")
    private List<Offer> offers;


}
