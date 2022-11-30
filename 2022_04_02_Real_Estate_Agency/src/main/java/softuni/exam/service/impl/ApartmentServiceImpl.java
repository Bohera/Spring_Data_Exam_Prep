package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ApartmentImportDto;
import softuni.exam.models.dto.ApartmentImportWrapperDto;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;

import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.validation.Validator;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.APARTMENTS_XML_PATH;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository apartmentRepository;

    private final TownRepository townRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;

    public ApartmentServiceImpl(
            ApartmentRepository apartmentRepository,
            TownRepository townRepository,
            Validator validator,
            ModelMapper modelMapper) {
        this.apartmentRepository = apartmentRepository;
        this.townRepository = townRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(APARTMENTS_XML_PATH);
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(ApartmentImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        ApartmentImportWrapperDto apartmentDtos =
                (ApartmentImportWrapperDto) unmarshaller.unmarshal(APARTMENTS_XML_PATH.toFile());

        List<ApartmentImportDto> apartments = apartmentDtos.getApartments();

        List<String> results = new ArrayList<>();

        for (ApartmentImportDto apartmentImportDto : apartments) {
            Set<ConstraintViolation<ApartmentImportDto>> errors =
                    this.validator.validate(apartmentImportDto);

            if(errors.isEmpty()) {
                Optional<Apartment> optionalApartment = this.apartmentRepository.findByTown_TownNameAndArea(
                        apartmentImportDto.getTownName(),
                        apartmentImportDto.getArea());

                if(optionalApartment.isEmpty()) {
                    Apartment apartment = this.modelMapper.map(apartmentImportDto, Apartment.class);

                    Optional<Town> town = this.townRepository.findByTownName(
                            apartmentImportDto.getTownName());

                    apartment.setTown(town.get());

                    this.apartmentRepository.save(apartment);

                    results.add(String.format("Successfully imported apartment %s - %.2f",
                            apartment.getApartmentType(),
                            apartment.getArea()));
                } else {
                    results.add("Invalid apartment");
                }
            } else {
                results.add("Invalid apartment");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
