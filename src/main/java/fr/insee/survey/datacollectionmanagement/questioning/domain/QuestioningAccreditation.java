package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idContact_index", columnList = "idContact"),
        @Index(name = "questioning_index", columnList = "questioning_id")
})
public class QuestioningAccreditation {

    @Id
    @GeneratedValue
    private Long id;

    private boolean isMain;
    private Date creationDate;
    private String creationAuthor;
    @NonNull
    private String idContact;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    private Questioning questioning;

    @Override
    public String toString() {
        return "QuestioningAccreditation [id=" + id + ", isMain=" + isMain + ", creationDate=" + creationDate
                + ", creationAuthor=" + creationAuthor + ", idContact=" + idContact + "]";
    }

}
