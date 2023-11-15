package fr.insee.survey.datacollectionmanagement.contact.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class ContactEvent {

    public enum ContactEventType {
        create, update, merged, firstConnect, reinitPassword
    }

    @Id
    @GeneratedValue
    private Long id;
    private Date eventDate;
    @NonNull
    @Enumerated(EnumType.STRING)
    private ContactEventType type;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Contact contact;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    @Override
    public String toString() {
        return "ContactEvent [id=" + id + ", eventDate=" + eventDate + ", type=" + type.name()
                + ", payload=" + payload.toString() + "]";
    }


}
