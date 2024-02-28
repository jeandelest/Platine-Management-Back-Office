package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.query.service.SearchContactService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchContactServiceImpl implements SearchContactService {

    private final ContactService contactService;

    private final PartitioningService partioningService;

    private final QuestioningAccreditationService questioningAccreditationService;


    @Override
    public Page<SearchContactDto> searchContactCrossDomain(
            String identifier,
            String name,
            String email,
            Pageable pageable) {

        List<SearchContactDto> listSearchContact = new ArrayList<>();

        Page<Contact> pageContact = null;

        if (!StringUtils.isEmpty(identifier)) {
            Contact c = contactService.findByIdentifier(identifier);
            pageContact = new PageImpl<>(Arrays.asList(c),pageable,1);
        } else if (!StringUtils.isEmpty(email)) {
            pageContact = contactService.findByEmail(email, pageable);

        } else if (!StringUtils.isEmpty(name)) {
            pageContact = contactService.findByName(name, pageable);
        } else {
            pageContact = contactService.findAll(pageable);
        }
        assert pageContact != null;
        for (Contact c : pageContact) {
            listSearchContact.add(transformContactTSearchContactDto(c));
        }

        return new PageImpl<>(listSearchContact, pageable, pageContact.getTotalElements());
    }

    private SearchContactDto transformContactTSearchContactDto(Contact c) {
        SearchContactDto searchContact = new SearchContactDto();
        searchContact.setIdentifier(c.getIdentifier());
        searchContact.setFirstName(c.getFirstName());
        searchContact.setLastName(c.getLastName());
        searchContact.setEmail(c.getEmail());
        searchContact.setFunction(c.getFunction());
        List<QuestioningAccreditation> listAccreditations = questioningAccreditationService.findByContactIdentifier(c.getIdentifier());
        searchContact.setListSurveyUnitNames(listAccreditations.stream().map(a -> a.getQuestioning().getSurveyUnit().getIdSu()).toList());
        searchContact.setListSourcesId(listAccreditations.stream().
                map(a ->
                        partioningService.findById(a.getQuestioning().getIdPartitioning()).getCampaign().getSurvey().getSource().getId()).toList());
        return searchContact;
    }
}
