package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitServiceImpl implements SurveyUnitService {

    private final SurveyUnitRepository surveyUnitRepository;

    private final SurveyUnitAddressRepository surveyUnitAddressRepository;

    @Override
    public Optional<SurveyUnit> findbyId(String idSu) {
        return surveyUnitRepository.findById(idSu);
    }

    @Override
    public List<SurveyUnit> findbyIdentificationCode(String identificationCode) {
        return surveyUnitRepository.findAllByIdentificationCode(identificationCode);
    }

    @Override
    public List<SurveyUnit> findbyIdentificationName(String identificationName) {
        return surveyUnitRepository.findByIdentificationNameIgnoreCase(identificationName);
    }

    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return surveyUnitRepository.findAll(pageable);
    }

    @Override
    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit) {
        return surveyUnitRepository.save(surveyUnit);
    }

    @Override
    public SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit) {

        if (surveyUnit.getSurveyUnitAddress() != null) {

            Optional<SurveyUnit> existingSurveyUnit = findbyId(surveyUnit.getIdSu());
            if (existingSurveyUnit.isPresent()) {
                if (existingSurveyUnit.get().getSurveyUnitAddress() != null) {
                    surveyUnit.getSurveyUnitAddress().setId(existingSurveyUnit.get().getSurveyUnitAddress().getId());
                }
            } else
                log.info("Survey unit does not exist");

            surveyUnitAddressRepository.save(surveyUnit.getSurveyUnitAddress());
        }
        return surveyUnitRepository.save(surveyUnit);
    }

    @Override
    public void deleteSurveyUnit(String id) {
        surveyUnitRepository.deleteById(id);

    }

}
