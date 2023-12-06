package fr.insee.survey.datacollectionmanagement.contact.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class PayloadUtil {

    private PayloadUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static JsonNode getPayloadAuthor(Authentication auth) {
        Map<String, String> mapAuthor = new HashMap<>();
        mapAuthor.put("author", auth.getName());
        return new ObjectMapper().valueToTree(mapAuthor);
    }

}
