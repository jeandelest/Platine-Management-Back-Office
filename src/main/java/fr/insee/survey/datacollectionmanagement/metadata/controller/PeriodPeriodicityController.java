package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
public class PeriodPeriodicityController {

    @Operation(summary = "Search for periodicities")
    @GetMapping(value = Constants.API_PERIODICITIES, produces = "application/json")
    public ResponseEntity<String> getPeriodicities()  {
        JSONArray jsonArray = new JSONArray();
        for (PeriodicityEnum periodicity : PeriodicityEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("key", periodicity.name());
            json.put("label", periodicity.getValue());
            jsonArray.add(json);
        }
        return ResponseEntity.ok().body(jsonArray.toString());
    }

    @Operation(summary = "Search for periods")
    @GetMapping(value = Constants.API_PERIODS, produces = "application/json")
    public ResponseEntity<String> getPeriods()  {
        JSONArray jsonArray = new JSONArray();

        for (PeriodEnum period : PeriodEnum.values()) {
            JSONObject json = new JSONObject();
            json.put("key", period.name());
            json.put("label", period.getValue());
            json.put("period",period.getPeriod().name());
            jsonArray.add(json);
        }
        return ResponseEntity.ok().body(jsonArray.toString());
    }

    @Operation(summary = "Search for periods of a periodicity")
    @GetMapping(value = Constants.API_PERIODICITIES_ID_PERIODS, produces = "application/json")
    public ResponseEntity<String> getPeriodsOfPeriodicity(String periodicity) {
        try {
            PeriodicityEnum.valueOf(periodicity);
            JSONArray jsonArray = new JSONArray();
            for (PeriodEnum period : PeriodEnum.values()) {
                if (period.getPeriod().equals(PeriodicityEnum.valueOf(periodicity))) {
                    JSONObject json = new JSONObject();
                    json.put("key", period.name());
                    json.put("label", period.getValue());
                    json.put("period",period.getPeriod().name());
                    jsonArray.add(json);
                }          
            }
            return ResponseEntity.ok().body(jsonArray.toString());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("periodicity does not exist");
        }

    }

}
