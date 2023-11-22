package fr.insee.survey.datacollectionmanagement.user.validation;

import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator <UserRoleValid, String>{


    @Override
    public void initialize(UserRoleValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return false;
        return Arrays.stream(UserEvent.UserEventType.values()).anyMatch( v -> value.equals(v));
    }
}
