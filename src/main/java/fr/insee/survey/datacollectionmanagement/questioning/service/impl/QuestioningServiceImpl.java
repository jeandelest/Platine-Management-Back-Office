package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestioningServiceImpl implements QuestioningService {

    private final QuestioningRepository questioningRepository;

    private final SurveyUnitService surveyUnitService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ApplicationConfig applicationConfig;

    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return questioningRepository.findAll(pageable);
    }

    @Override
    public Questioning findbyId(Long id) {
        return questioningRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", id)));
    }

    @Override
    public Questioning saveQuestioning(Questioning questioning) {
        return questioningRepository.save(questioning);
    }

    @Override
    public void deleteQuestioning(Long id) {
        questioningRepository.deleteById(id);
    }

    @Override
    public Set<Questioning> findByIdPartitioning(String idPartitioning) {
        return questioningRepository.findByIdPartitioning(idPartitioning);
    }

    @Override
    public Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                             String surveyUnitIdSu) {
        return questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(idPartitioning,
                surveyUnitIdSu);
    }

    @Override
    public int deleteQuestioningsOfOnePartitioning(Partitioning partitioning) {
        int nbQuestioningDeleted = 0;
        Set<Questioning> setQuestionings = findByIdPartitioning(partitioning.getId());
        for (Questioning q : setQuestionings) {
            SurveyUnit su = q.getSurveyUnit();
            su.getQuestionings().remove(q);
            surveyUnitService.saveSurveyUnit(su);
            q.getQuestioningEvents().stream().forEach(qe -> questioningEventService.deleteQuestioningEvent(qe.getId()));
            q.getQuestioningAccreditations().stream()
                    .forEach(questioningAccreditationService::deleteAccreditation);
            deleteQuestioning(q.getId());
            nbQuestioningDeleted++;
        }
        return nbQuestioningDeleted;
    }

    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return questioningRepository.findBySurveyUnitIdSu(idSu);
    }

    /**
     * Generates an access URL based on the provided parameters.
     *
     * @param baseUrl      The base URL for the access.
     * @param typeUrl      The type of URL (V1 or V2).
     * @param role          The user role (REVIEWER or INTERVIEWER).
     * @param questioning   The questioning object.
     * @param surveyUnitId  The survey unit ID.
     * @return The generated access URL.
     */
    public String getAccessUrl(String baseUrl, String typeUrl, String role, Questioning questioning, String surveyUnitId) {
        // Set default values if baseUrl or typeUrl is empty
        baseUrl = StringUtils.defaultIfEmpty(baseUrl, applicationConfig.getQuestioningUrl());
        typeUrl = StringUtils.defaultIfEmpty(typeUrl, "V2");

        if (typeUrl.equalsIgnoreCase("V1")) {
            return buildV1Url(baseUrl, role, questioning.getModelName(), surveyUnitId);
        } else if (typeUrl.equalsIgnoreCase("V2")) {
            return buildV2Url(baseUrl, role, questioning.getModelName(), surveyUnitId);
        }

        return "";
    }

    /**
     * Builds a V1 access URL based on the provided parameters.
     *
     * @param baseUrl      The base URL for the access.
     * @param role          The user role (REVIEWER or INTERVIEWER).
     * @param campaignId    The campaign ID.
     * @param surveyUnitId  The survey unit ID.
     * @return The generated V1 access URL.
     */
    private String buildV1Url(String baseUrl, String role, String campaignId, String surveyUnitId) {
        if (role.equalsIgnoreCase(UserRoles.REVIEWER)) {
            return baseUrl + "/visualiser/" + campaignId + "/" + surveyUnitId;
        } else if (role.equalsIgnoreCase(UserRoles.INTERVIEWER)) {
            return baseUrl + "/repondre/" + campaignId + "/" + surveyUnitId;
        }
        return "";
    }

    /**
     * Builds a V2 access URL based on the provided parameters.
     *
     * @param baseUrl      The base URL for the access.
     * @param role          The user role (REVIEWER or INTERVIEWER).
     * @param modelName     The model name from the questioning object.
     * @param surveyUnitId  The survey unit ID.
     * @return The generated V2 access URL.
     */
    private String buildV2Url(String baseUrl, String role, String modelName, String surveyUnitId) {
        if (role.equalsIgnoreCase(UserRoles.REVIEWER)) {
            return baseUrl + "/readonly/questionnaire/" + modelName + "/unite-enquetee/" + surveyUnitId;
        } else if (role.equalsIgnoreCase(UserRoles.INTERVIEWER)) {
            return baseUrl + "/questionnaire/" + modelName + "/unite-enquetee/" + surveyUnitId;
        }
        return "";
    }


 }
