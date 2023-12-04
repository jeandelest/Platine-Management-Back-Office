package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CampaignEvent {

    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String type;
    @NonNull
    private Date date;

    @ManyToOne
    private Campaign campaign;

}
