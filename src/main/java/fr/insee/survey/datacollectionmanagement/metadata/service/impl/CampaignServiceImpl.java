package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    PartitioningService partitioningService;

    public Collection<CampaignMoogDto> getCampaigns() {

        List<CampaignMoogDto> moogCampaigns = new ArrayList<>();
        List<Campaign> campaigns = campaignRepository.findAll().stream().filter(c -> !c.getPartitionings().isEmpty()).toList();

        for (Campaign campaign : campaigns) {
            CampaignMoogDto campaignMoogDto = new CampaignMoogDto();
            campaignMoogDto.setId(campaign.getId());
            campaignMoogDto.setLabel(campaign.getCampaignWording());

            Optional<Date> dateMin = campaign.getPartitionings().stream().map(Partitioning::getOpeningDate)
                    .min(Comparator.comparing(Date::getTime));
            Optional<Date> dateMax = campaign.getPartitionings().stream().map(Partitioning::getClosingDate)
                    .max(Comparator.comparing(Date::getTime));

            if (dateMin.isPresent() && dateMax.isPresent()) {
                campaignMoogDto.setCollectionStartDate(dateMin.get().getTime());
                campaignMoogDto.setCollectionEndDate(dateMax.get().getTime());
                moogCampaigns.add(campaignMoogDto);
            } else {
                log.warn("No start date or end date found for campaign {}", campaign.getId());
            }
        }
        return moogCampaigns;
    }

    @Override
    public List<Campaign> findbyPeriod(String period) {
        return campaignRepository.findByPeriod(period);
    }

    @Override
    public Optional<Campaign> findById(String idCampaign) {
        return campaignRepository.findById(idCampaign);
    }

    @Override
    public List<Campaign> findbySourceYearPeriod(String source, Integer year, String period) {
        return campaignRepository.findBySourceYearPeriod(source, year, period);
    }

    @Override
    public List<Campaign> findbySourcePeriod(String source, String period) {
        return campaignRepository.findBySourcePeriod(source, period);
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return campaignRepository.findAll(pageable);
    }

    @Override
    public Campaign insertOrUpdateCampaign(Campaign campaign) {
        Optional<Campaign> campaignBase = findById(campaign.getId());
        if (!campaignBase.isPresent()) {
            log.info("Create campaign with the id {}", campaign.getId());
            return campaignRepository.save(campaign);
        }
        log.info("Update campaign with the id {}", campaign.getId());
        campaign.setPartitionings(campaignBase.get().getPartitionings());
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaignById(String id) {
        campaignRepository.deleteById(id);
    }

    @Override
    public Campaign addPartitionigToCampaign(Campaign campaign, Partitioning partitioning) {
        Optional<Campaign> campaignBase = findById(campaign.getId());
        if (campaignBase.isPresent() && isPartitioningPresent(partitioning, campaignBase.get())) {
            campaign.setPartitionings(campaignBase.get().getPartitionings());
        } else {
            Set<Partitioning> partitionings = (!campaignBase.isPresent()) ? new HashSet<>()
                    : campaignBase.get().getPartitionings();
            partitionings.add(partitioning);
            campaign.setPartitionings(partitionings);
        }
        return campaign;
    }

    private boolean isPartitioningPresent(Partitioning p, Campaign c) {
        for (Partitioning part : c.getPartitionings()) {
            if (part.getId().equals(p.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCampaignOngoing(String idCampaign) throws NotFoundException {
        Optional<Campaign> camp = findById(idCampaign);

        if (camp.isEmpty()) {
            throw new NotFoundException("Campaign does not exist");
        }
        Date now = new Date();
        int nbOnGoingParts = 0;

        for (Partitioning part : camp.get().getPartitionings()) {
            if (partitioningService.isOnGoing(part, now)) {
                nbOnGoingParts++;
                log.info("Partitiong {}  of campaign {} is ongoing", part.getId(), idCampaign);
            } else {
                log.info("Partitiong {}  of campaign {}  is closed", part.getId(), idCampaign);
            }
        }
        return !camp.get().getPartitionings().isEmpty() && nbOnGoingParts == camp.get().getPartitionings().size();
    }

}
