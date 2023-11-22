package fr.insee.survey.datacollectionmanagement.user.dto;


import fr.insee.survey.datacollectionmanagement.user.validation.UserRoleValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto{
    @NotBlank
    private String identifier;
    @UserRoleValid
    private String role;

}

