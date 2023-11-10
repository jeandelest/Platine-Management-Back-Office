package fr.insee.survey.datacollectionmanagement.questioning.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Operator {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;


}
