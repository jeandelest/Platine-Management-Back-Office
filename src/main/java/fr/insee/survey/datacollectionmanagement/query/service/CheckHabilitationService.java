package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public interface CheckHabilitationService {

    ResponseEntity<HabilitationDto> checkHabilitation(String role, String idSu, String campaign);

}
