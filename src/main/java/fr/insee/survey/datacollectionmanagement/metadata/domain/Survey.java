package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "surveyyear_index", columnList = "year_value"),
        @Index(name = "source_index", columnList = "source_id")
})
public class Survey {

    @Id
    private String id;
    @Column(name = "YEAR_VALUE")
    @NonNull
    private Integer year;
    private Integer sampleSize;
    @Column(length = 2000)
    private String longWording;
    @Column(length = 2000)
    private String shortWording;
    @Column(length = 2000)
    private String shortObjectives;
    @Column(length = 2000)
    private String longObjectives;
    private String visaNumber;
    private String cnisUrl;
    private String diffusionUrl;
    private String noticeUrl;
    private String specimenUrl;
    private String communication;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Campaign> campaigns;

    @ManyToOne
    private Source source;

}
