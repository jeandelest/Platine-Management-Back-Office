package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.config.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogFollowUpDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogProgressDto;
import fr.insee.survey.datacollectionmanagement.query.service.MonitoringService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "5 - Moog", description = "Enpoints for moog")
@Slf4j
public class MonitoringController {

    @Autowired
    MonitoringService monitoringService;

    @Autowired
    QuestioningService questioningService;

    @Autowired
    PartitioningService partitioningService;

    @Autowired
    CampaignService campaignService;

    @GetMapping(value = "/api/moog/campaigns/{idCampaign}/monitoring/progress", produces = "application/json")
    public JSONCollectionWrapper<MoogProgressDto> getDataForProgress(@PathVariable String idCampaign) {
        log.info("Request GET for monitoring moog progress table for campaign : {}", idCampaign);
        return monitoringService.getProgress(idCampaign);
    }

    @GetMapping(value = "/api/moog/campaigns/{idCampaign}/monitoring/follow-up", produces = "application/json")
    public JSONCollectionWrapper<MoogFollowUpDto> getDataToFollowUp(@PathVariable String idCampaign) {
        log.info("Request GET for following table for campaign : {}", idCampaign);
        return monitoringService.getFollowUp(idCampaign);
    }

    @GetMapping(value = "/api/temp/moog/campaigns/{idCampaign}/monitoring/progress", produces = "application/json")
    public JSONCollectionWrapper<MoogProgressDto> getDataForProgressTemp(@PathVariable String idCampaign) {
        log.info("Request GET for monitoring moog progress table for campaign : {}", idCampaign);
        Optional<Campaign> campaign = campaignService.findById(idCampaign);
        if (campaign.isEmpty()) {
            throw new NoSuchElementException("campaign does not exist");
        }
        log.info("{} partitionings found", campaign.get().getPartitionings().stream().map(Partitioning::getId)
                .toList().size());
        campaign.get().getPartitionings().forEach(part -> log.info("{} partitionig found", part.getId()));

        return null;
    }

    @GetMapping(value = "/api/temp/moog/campaigns/{idCampaign}/monitoring/follow-up", produces = "application/json")
    public JSONCollectionWrapper<MoogFollowUpDto> getDataToFollowUpTemp(@PathVariable String idCampaign) {
        log.info("Request GET for following table for campaign : {}", idCampaign);
        return monitoringService.getFollowUp(idCampaign);
    }
}
