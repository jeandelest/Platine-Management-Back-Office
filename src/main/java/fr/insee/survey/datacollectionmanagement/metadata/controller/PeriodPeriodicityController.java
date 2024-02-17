package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.PeriodDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.PeriodicityDto;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
public class PeriodPeriodicityController {

    @Operation(summary = "Search for periodicities")
    @GetMapping(value = Constants.API_PERIODICITIES, produces = "application/json")
    public ResponseEntity<List<PeriodicityDto>> getPeriodicities()  {
        List<PeriodicityDto> periodicities = new ArrayList<>();
        for (PeriodicityEnum periodicity : PeriodicityEnum.values()) {
            periodicities.add(new PeriodicityDto(periodicity.getValue(),periodicity.name()));
        }
        return ResponseEntity.ok().body(periodicities);
    }

    @Operation(summary = "Search for periods")
    @GetMapping(value = Constants.API_PERIODS, produces = "application/json")
    public ResponseEntity<List<PeriodDto>> getPeriods()  {
        List<PeriodDto> periods = new ArrayList<>();
        for (PeriodEnum period : PeriodEnum.values()) {
            periods.add(new PeriodDto(period.getValue(),period.name(), period.getPeriod().getValue()));
        }
        return ResponseEntity.ok().body(periods);
    }

    @Operation(summary = "Search for periods of a periodicity")
    @GetMapping(value = Constants.API_PERIODICITIES_ID_PERIODS, produces = "application/json")
    public ResponseEntity<List<PeriodDto>> getPeriodsOfPeriodicity(String periodicity) {
        try {
            PeriodicityEnum.valueOf(periodicity);
            List<PeriodDto> periods = new ArrayList<>();
            for (PeriodEnum period : PeriodEnum.values()) {
                if (period.getPeriod().equals(PeriodicityEnum.valueOf(periodicity))) {
                    periods.add(new PeriodDto(period.getValue(),period.name(), period.getPeriod().getValue()));
                }          
            }
            return ResponseEntity.ok().body(periods);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("periodicity does not exist");
        }

    }

}
