package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
public class SurveyUnitAddress {

    @Id
    @GeneratedValue
    private Long id;

    private String streetNumber;
    private String repetitionIndex;
    private String streetType;
    private String streetName;
    private String addressSupplement;
    private String cityName;
    private String zipCode;
    private String cedexCode;
    private String cedexName;
    private String specialDistribution;
    private String countryCode;
    private String countryName;
}
