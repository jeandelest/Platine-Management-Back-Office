package fr.insee.survey.datacollectionmanagement.metadata.domain;

import java.util.Set;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Support {

    @Id
    private String id;

    private String label;
    private String phoneNumber;
    private String mail;
    private String countryName;
    private String streetNumber;
    private String streetName;
    private String city;
    private String zipCode;

    @OneToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Source> sources;

}
