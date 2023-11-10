package fr.insee.survey.datacollectionmanagement.metadata.domain;

import java.util.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Data
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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Campaign campaign;

}
