package softuni.exam.config;

import com.google.gson.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationBeanConfiguration {


    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Gson createGson() {

        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JsonDeserializer<LocalDate> toLocalTime =
                (json, t, c) -> LocalDate.parse(json.getAsString(), dateFormat);

        JsonSerializer<String> fromLocalTime =
                (date, t, c) -> new JsonPrimitive(date);

        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, toLocalTime)
                .registerTypeAdapter(LocalDate.class, fromLocalTime)
                .create();
    }

    @Bean
    public Validator createValidation() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

}

