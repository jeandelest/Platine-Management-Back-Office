package fr.insee.survey.datacollectionmanagement.metadata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenDto {
    private boolean surveyOnline;
    private String messageSurveyOffline;
    private String messageInfoSurveyOffline;

}
