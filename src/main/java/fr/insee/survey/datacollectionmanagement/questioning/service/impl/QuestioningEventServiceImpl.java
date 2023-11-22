package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.util.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestioningEventServiceImpl implements QuestioningEventService {

    @Autowired
    LastQuestioningEventComparator lastQuestioningEventComparator;

    @Autowired
    QuestioningEventRepository questioningEventRepository;

    @Override
    public Optional<QuestioningEvent> findbyId(Long id) {
        return questioningEventRepository.findById(id);
    }

    @Override
    public QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent) {
        return questioningEventRepository.save(questioningEvent);
    }

    @Override
    public void deleteQuestioningEvent(Long id) {
        questioningEventRepository.deleteById(id);

    }

    @Override
    public Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning,
            List<TypeQuestioningEvent> events) {

        List<QuestioningEvent> listQuestioningEvent = questioning.getQuestioningEvents().stream()
                .filter(qe -> events.contains(qe.getType())).sorted(lastQuestioningEventComparator).toList();
        return listQuestioningEvent.stream().findFirst();
    }

    @Override
    public List<QuestioningEvent> findbyIdUpload(Long id){
        return questioningEventRepository.findAll().stream().filter(qe -> qe.getUpload()!= null && qe.getUpload().getId().equals(id)).toList();
    }

}
