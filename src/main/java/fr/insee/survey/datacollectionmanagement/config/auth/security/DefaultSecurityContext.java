package fr.insee.survey.datacollectionmanagement.config.auth.security;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.auth.user.User;
import fr.insee.survey.datacollectionmanagement.config.auth.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnMissingBean(OpenIDConnectSecurityContext.class)
@AllArgsConstructor
public class DefaultSecurityContext {

    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    @Autowired
    ApplicationConfig config;
    /**
     * Configure spring security filter chain when no authentication
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception
     */
    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(csrfConfig -> csrfConfig.disable())
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'")
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        ))
                .anonymous(anonymousConfig -> anonymousConfig
                        .authorities("ROLE_ADMIN"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }
    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http) throws Exception {
        System.out.println(publicUrls());
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, publicUrls());
    }
    @Bean
    public UserProvider getUserProvider() {
        return auth -> new User();
    }

    private String[] publicUrls(){
        String[] str = new String[config.getPublicUrls().size()];
        for (int i = 0; i < config.getPublicUrls().size(); i++) {
            str[i] = config.getPublicUrls().get(i);
        }
        return str;

    }



}