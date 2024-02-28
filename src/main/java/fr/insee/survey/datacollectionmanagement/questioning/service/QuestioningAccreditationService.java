package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestioningAccreditationService {

    public List<QuestioningAccreditation> findByContactIdentifier(String id);


    public Page<QuestioningAccreditation> findAll(Pageable pageable);

    public QuestioningAccreditation findById(Long id);
   
    public QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation);

    public void deleteAccreditation(QuestioningAccreditation c);

}
