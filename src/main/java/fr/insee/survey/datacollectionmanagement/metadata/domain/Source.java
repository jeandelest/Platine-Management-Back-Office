package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Source {

    @Id
    private String id;
    private String longWording;
    private String shortWording;
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodicityEnum periodicity;
    @NonNull
    private Boolean mandatoryMySurveys;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Survey> surveys;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SourceAccreditation> sourceAccreditations;

    @ManyToOne
    @NonNull
    private Owner owner;

    @ManyToOne
    @NonNull
    private Support support;

}
