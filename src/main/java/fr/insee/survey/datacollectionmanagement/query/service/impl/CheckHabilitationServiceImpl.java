package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckHabilitationServiceImpl implements CheckHabilitationService {

    private final ApplicationConfig applicationConfig;

    private final ViewService viewService;

    private final UserService userService;

    @Override
    public ResponseEntity<HabilitationDto> checkHabilitation(String role, String idSu, String campaignId) {

        HabilitationDto resp = new HabilitationDto();

        //noauth
        if (!applicationConfig.getAuthType().equals(AuthConstants.OIDC)) {
            resp.setHabilitated(true);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList(applicationConfig.getRoleClaim());
        String idec = jwt.getClaimAsString(applicationConfig.getIdClaim()).toUpperCase();

        //admin
        if (isUserInRole(roles, applicationConfig.getRoleAdmin())) {
            log.info("Check habilitation of admin {} for accessing survey-unit {} of campaign {} resulted in true", idec, idSu, campaignId);
            resp.setHabilitated(true);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }

        //respondents
        if (role == null || role.isBlank() || role.equals(Constants.INTERVIEWER)) {
            if (isUserInRole(roles, applicationConfig.getRoleRespondent())) {
                boolean habilitated = viewService.countViewByIdentifierIdSuCampaignId(idec, idSu, campaignId) != 0;
                log.info("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} resulted in {}", idec, idSu, campaignId, habilitated);
                resp.setHabilitated(habilitated);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            }
            log.warn("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} - no respondent habilitation found in token - check habilitation: false", idec, idSu, campaignId);
            resp.setHabilitated(false);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }


        // internal users
        Optional<User> user = userService.findByIdentifier(idec);
        if (!role.equals(Constants.REVIEWER)) {
            resp.setHabilitated(false);
            log.warn("User {}  -  internal user habilitation not found in token - Check habilitation:false", idec);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        if (isUserInRole(roles, applicationConfig.getRoleInternalUser())) {
            if (user.isPresent()) {
                String userRole;
                //List<String> accreditedSources = new ArrayList<>();
                userRole = user.get().getRole().toString();
                if (userRole.equals(User.UserRoleType.assistance)) {
                    resp.setHabilitated(false);
                    log.warn("User '{}' has assistance profile - check habilitation: false", idec);
                    return new ResponseEntity<>(resp, HttpStatus.OK);
                }
                resp.setHabilitated(true);
                log.warn("User '{}' has {} profile - check habilitation: true", idec, userRole);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            }

            resp.setHabilitated(false);
            log.warn("User '{}' doesn't exists", idec);
            return new ResponseEntity<>(resp, HttpStatus.OK);


        }

        resp.setHabilitated(false);
        log.warn("Only '{}' ans '{}' are accepted as a role in query argument", Constants.REVIEWER, Constants.INTERVIEWER);
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    private boolean isUserInRole(List<String> roles, List<String> role) {

        return role.stream().anyMatch(r -> roles.contains(r));
    }

}
