package fr.insee.survey.datacollectionmanagement.user.domain;


import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;


import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class SourceAccreditation {

    @Id
    @GeneratedValue
    private Long id;

    private Date creationDate;
    private String creationAuthor;
    @NonNull
    private String idUser;

    @OneToOne
    @EqualsAndHashCode.Exclude
    private Source source;

    @Override
    public String toString() {
        return "SourceAccreditation{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", creationAuthor='" + creationAuthor + '\'' +
                ", idUser='" + idUser + '\'' +
                ", source=" + source +
                '}';
    }
}
