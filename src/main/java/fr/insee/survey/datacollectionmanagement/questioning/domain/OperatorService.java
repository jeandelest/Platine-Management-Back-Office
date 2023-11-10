package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
public class OperatorService {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String mail;

    @OneToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Operator> operators;
}
