package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.repository.OwnerRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public Optional<Owner> findById(String owner) {
        return ownerRepository.findById(owner);
    }

    @Override
    public Page<Owner> findAll(Pageable pageable) {
        return ownerRepository.findAll(pageable);
    }

    @Override
    public Owner insertOrUpdateOwner(Owner owner) {
        Optional<Owner> ownerBase = findById(owner.getId());
        if (!ownerBase.isPresent()) {
            log.info("Create owner with the id {}", owner.getId());
            return ownerRepository.save(owner);
        }
        log.info("Update owner with the id {}", owner.getId());
        owner.setSources(ownerBase.get().getSources());
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteOwnerById(String id) {
        ownerRepository.deleteById(id);

    }

    @Override
    public void removeSourceFromOwner(Owner owner, Source source) {
        if (owner != null && owner.getSources() != null) {
            owner.getSources().remove(source);
            ownerRepository.save(owner);
        }
    }

    @Override
    public void addSourceFromOwner(Owner owner, Source source) {
        owner.getSources().add(source);
        ownerRepository.save(owner);
    }

}
