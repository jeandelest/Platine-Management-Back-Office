package fr.insee.survey.datacollectionmanagement.user.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class UserEvent {

    public enum UserEventType {
        create, update, delete
    }

    @Id
    @GeneratedValue
    private Long id;

    private Date eventDate;
    @NonNull
    @Enumerated(EnumType.STRING)
    private UserEventType type;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", type=" + type +
                ", user=" + user +
                ", payload=" + payload +
                '}';
    }
}
