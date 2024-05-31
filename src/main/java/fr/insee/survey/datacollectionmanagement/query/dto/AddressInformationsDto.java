package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonRootName("Address")
public class AddressInformationsDto {

    @JsonProperty("L1")
    private String lineOne;
    @JsonProperty("L2")
    private String lineTwo;
    @JsonProperty("L3")
    private String lineThree;
    @JsonProperty("L4")
    private String lineFour;
    @JsonProperty("L5")
    private String lineFive;

}
