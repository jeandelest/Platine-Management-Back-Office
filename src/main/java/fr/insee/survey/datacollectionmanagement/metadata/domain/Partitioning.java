package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(indexes = {
        @Index(name = "campainId_index", columnList = "campaign_id")
})
public class Partitioning {

    @Id
    private String id;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;

    @ManyToOne
    private Campaign campaign;

}
