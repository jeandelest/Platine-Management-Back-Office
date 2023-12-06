package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SurveyDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    private final SourceService sourceService;

    private final ViewService viewService;

    private final ModelMapper modelmapper;

    private final QuestioningService questioningService;

    @Operation(summary = "Search for surveys by the source id")
    @GetMapping(value = Constants.API_SOURCES_ID_SURVEYS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SurveyDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> getSurveysBySource(@PathVariable("id") String id) {
        Source source = sourceService.findById(id);
        try {
            return ResponseEntity.ok()
                    .body(source.getSurveys().stream().map(this::convertToDto).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }

    }

    @Operation(summary = "Search for a survey by its id")
    @GetMapping(value = Constants.API_SURVEYS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> getSurvey(@PathVariable("id") String id) {
        Survey survey = surveyService.findById(StringUtils.upperCase(id));
        try {
            return ResponseEntity.ok().body(convertToDto(survey));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }

    }

    @Operation(summary = "Update or create a survey")
    @PutMapping(value = Constants.API_SURVEYS_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = SurveyDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<?> putSurvey(@PathVariable("id") String id, @RequestBody SurveyDto surveyDto) {
        if (StringUtils.isBlank(surveyDto.getId()) || !surveyDto.getId().equalsIgnoreCase(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id and idSurvey don't match");
        }
        Survey survey;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(surveyDto.getId()).toUriString());
        HttpStatus httpStatus;

        try {
            surveyService.findById(id);
            log.info("Update survey with the id {}", surveyDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Creating survey with the id {}", surveyDto.getId());
            httpStatus = HttpStatus.CREATED;
        }

        survey = surveyService.insertOrUpdateSurvey(convertToEntity(surveyDto));
        Source source = survey.getSource();
        source.getSurveys().add(survey);
        sourceService.insertOrUpdateSource(source);
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(survey));
    }

    @Operation(summary = "Delete a survey, its campaigns, partitionings, questionings ...")
    @DeleteMapping(value = Constants.API_SURVEYS_ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @Transactional
    public ResponseEntity<?> deleteSurvey(@PathVariable("id") String id) {
        Survey survey = surveyService.findById(id);

        try {

            int nbQuestioningDeleted = 0;
            int nbViewDeleted = 0;

            Source source = survey.getSource();
            source.getSurveys().remove(survey);
            sourceService.insertOrUpdateSource(source);
            surveyService.deleteSurveyById(id);
            List<Partitioning> listPartitionings = new ArrayList<>();

            survey.getCampaigns().stream().forEach(c -> listPartitionings.addAll(c.getPartitionings()));

            for (Campaign campaign : survey.getCampaigns()) {
                viewService.findViewByCampaignId(campaign.getId()).stream()
                        .forEach(v -> viewService.deleteView(v));
            }
            for (Partitioning partitioning : listPartitionings) {
                questioningService.findByIdPartitioning(partitioning.getId()).stream()
                        .forEach(q -> questioningService.deleteQuestioning(q.getId()));
            }

            for (Campaign campaign : survey.getCampaigns()) {
                nbViewDeleted += viewService.deleteViewsOfOneCampaign(campaign);
            }
            for (Partitioning partitioning : listPartitionings) {
                nbQuestioningDeleted += questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
            }
            log.info("Source {} deleted and all its metadata children - {} questioning deleted - {} view deleted", id,
                    nbQuestioningDeleted, nbViewDeleted);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Survey deleted");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    private SurveyDto convertToDto(Survey survey) {
        return modelmapper.map(survey, SurveyDto.class);
    }

    private Survey convertToEntity(SurveyDto surveyDto) {
        return modelmapper.map(surveyDto, Survey.class);
    }

    class SurveyPage extends PageImpl<SurveyDto> {

        public SurveyPage(List<SurveyDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
