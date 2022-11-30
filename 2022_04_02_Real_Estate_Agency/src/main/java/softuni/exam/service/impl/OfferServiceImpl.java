package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.OfferImportDto;
import softuni.exam.models.dto.OfferImportWrapperDto;
import softuni.exam.models.entity.*;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;

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

import static softuni.exam.models.entity.ApartmentType.three_rooms;
import static softuni.exam.util.Paths.OFFERS_XML_PATH;

@Service
public class OfferServiceImpl implements OfferService {
    private final AgentRepository agentRepository;

    private final ApartmentRepository apartmentRepository;

    private final OfferRepository offerRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;

    public OfferServiceImpl(
            AgentRepository agentRepository,
            ApartmentRepository apartmentRepository,
            OfferRepository offerRepository,
            Validator validator,
            ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;
        this.offerRepository = offerRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(OFFERS_XML_PATH);
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(OfferImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        OfferImportWrapperDto offersDtos =
                (OfferImportWrapperDto) unmarshaller.unmarshal(OFFERS_XML_PATH.toFile());

        List<OfferImportDto> offers = offersDtos.getOffers();

        List<String> results = new ArrayList<>();

        for (OfferImportDto offerImportDto : offers) {
            Set<ConstraintViolation<OfferImportDto>> errors =
                    this.validator.validate(offerImportDto);

            if(errors.isEmpty()) {
                Optional<Agent> optionalAgent =
                        this.agentRepository.findByFirstName(offerImportDto.getAgent().getName());

                if(optionalAgent.isPresent()) {
                    Offer offer = this.modelMapper.map(offerImportDto, Offer.class);


                    Optional<Apartment> apartment =
                            this.apartmentRepository.findById(offerImportDto.getApartment().getId());

                    offer.setAgent(optionalAgent.get());
                    offer.setApartment(apartment.get());

                    this.offerRepository.save(offer);

                    results.add(String.format("Successfully imported offer %.2f",
                            offer.getPrice()));
                } else {
                    results.add("Invalid offer");
                }
            } else {
                results.add("Invalid offer");
            }
        }
        return String.join(System.lineSeparator(), results);
    }

    @Override
    public String exportOffers() {

        ApartmentType apartmentType = three_rooms;

        List<Offer> bestOffers = this.offerRepository.findAllByApartment_ApartmentTypeOrderByApartment_AreaDescPriceAsc(apartmentType);

        return bestOffers
                .stream()
                .map(Offer::toString)
                .collect(Collectors.joining("\n"));
    }
}
