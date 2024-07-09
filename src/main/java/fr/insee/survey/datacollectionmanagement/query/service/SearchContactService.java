package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchContactService {

    /**
     * Search contact according to different parameters
     * @param param search contact parameter (mail or identifier or name
     * @return Page SearchContactDto
     */
    Page<SearchContactDto> searchContactCrossDomain(
        String param,
        Pageable pageable);
}
