package fr.insee.survey.datacollectionmanagement.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserEventDto {

    private Long id;
    @NotBlank(message = "identifier can't be blank")
    private String identifier;
    private Date eventDate;

    private String type;
    private JsonNode payload;

}
