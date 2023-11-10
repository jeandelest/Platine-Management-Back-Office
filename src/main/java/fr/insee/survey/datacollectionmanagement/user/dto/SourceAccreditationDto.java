package fr.insee.survey.datacollectionmanagement.user.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SourceAccreditationDto {

    @JsonIgnore
    private Long id;

    private Date creationDate;
    private String creationAuthor;
    private String idUser;

}
