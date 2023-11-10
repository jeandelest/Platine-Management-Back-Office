package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(indexes = {
    @Index(name = "identificationName_index", columnList = "identificationName"), @Index(name = "identificationCode_index", columnList = "identificationCode")
})
public class SurveyUnit {

    @Id
    private String idSu;

    @OneToMany
    private Set<Questioning> questionings;

    private String identificationCode;

    // "Raison Sociale"
    private String identificationName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private SurveyUnitAddress surveyUnitAddress;

    @Override
    public String toString() {
        return "SurveyUnit [idSu=" + idSu + ", identificationCode=" + identificationCode + ", identificationName="
                + identificationName + ", surveyUnitAddress=" + surveyUnitAddress + "]";
    }
    
    

}
