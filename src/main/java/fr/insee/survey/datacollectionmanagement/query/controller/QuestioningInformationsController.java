package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.service.QuestioningInformationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| (@AuthorizeMethodDecider.isRespondent() && (#id == @AuthorizeMethodDecider.getUsername()))"
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Slf4j
@Tag(name = "6 - Webclients", description = "Enpoints for webclients")
@RequiredArgsConstructor
@Validated
public class QuestioningInformationsController {

    private final QuestioningInformationsService questioningInformationsService;

    @Operation(summary = "Informations to fill in an Orbeon questionnaire")
    @GetMapping(value = Constants.API_WEBCLIENT_INFORMATIONS, produces = MediaType.APPLICATION_XML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = QuestioningInformationsDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<QuestioningInformationsDto> getQuestioningInformations(@PathVariable("idCampaign") String idCampaign,
                                                                                 @PathVariable("idUE") String idsu) {

        QuestioningInformationsDto questioningInformationsDto = questioningInformationsService.findQuestioningInformations(idCampaign, idsu);

        return ResponseEntity.status(HttpStatus.OK).body(questioningInformationsDto);
    }
}
