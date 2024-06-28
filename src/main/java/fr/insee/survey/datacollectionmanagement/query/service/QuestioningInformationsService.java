package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;

public interface QuestioningInformationsService {
    QuestioningInformationsDto findQuestioningInformations(String idCampaign, String idsu);
}
