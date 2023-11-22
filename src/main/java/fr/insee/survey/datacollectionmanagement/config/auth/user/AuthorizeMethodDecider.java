package fr.insee.survey.datacollectionmanagement.config.auth.user;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("AuthorizeMethodDecider")
@Slf4j
@RequiredArgsConstructor
public class AuthorizeMethodDecider {

    public static final String ROLE_OFFLINE_ACCESS = "ROLE_offline_access";
    public static final String ROLE_UMA_AUTHORIZATION = "ROLE_uma_authorization";
    private User noAuthUser;
    private final UserProvider userProvider;

    @Autowired
    ApplicationConfig config;

    public User getUser() {
        if (config.getAuthType().equals(AuthConstants.OIDC)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userProvider.getUser(authentication);
            return currentUser;
        }
        return noAuthUser();
    }

    private User noAuthUser() {
        if (this.noAuthUser != null) {
            return this.noAuthUser;
        }

        List<String> roles = new ArrayList<>();
        roles.add(ROLE_OFFLINE_ACCESS);
        roles.add(config.getRoleAdmin().get(0));
        roles.add(ROLE_UMA_AUTHORIZATION);
        return new User("GUEST", roles);
    }

    public boolean isInternalUser() {
        User user = getUser();
        return isInternalUser(user);
    }

    public boolean isInternalUser(User user) {
        return (hasRole(user, config.getRoleInternalUser()));
    }

    public boolean isAdmin()  {
        User user = getUser();
        return isAdmin(user);
    }

    public boolean isAdmin(User user) {
        return (hasRole(user, config.getRoleAdmin()));
    }

    public boolean isWebClient() {
        User user = getUser();
        return isWebClient(user);
    }

    public boolean isWebClient(User user) {
        return hasRole(user, config.getRoleWebClient());
    }

    public boolean isRespondent() {
        User user = getUser();
        return isRespondent(user);
    }

    public boolean isRespondent(User user) {
        return hasRole(user, config.getRoleRespondent());
    }

    private boolean hasRole(User user, List<String> authorizedRoles) {
        Boolean hasRole = false;
        List<String> userRoles = user.getRoles();
        return userRoles.stream().filter(r -> authorizedRoles.contains(r)).count() > 0;
    }

    public String getUsername() {
        User user = getUser();
        return user.getId().toUpperCase();
    }

}
