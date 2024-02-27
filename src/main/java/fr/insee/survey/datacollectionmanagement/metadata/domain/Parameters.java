package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Parameters {

    @Getter
    public enum ParameterEnum {
        URL_REDIRECTION,URL_TYPE;
    }

    @Id
    private String metadataId;

    @Id
    @Enumerated(EnumType.STRING)
    private ParameterEnum paramId;
    private String paramValue;

}
