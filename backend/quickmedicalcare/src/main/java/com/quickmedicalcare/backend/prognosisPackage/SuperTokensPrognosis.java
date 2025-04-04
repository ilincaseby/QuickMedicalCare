package com.quickmedicalcare.backend.prognosisPackage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickmedicalcare.backend.config.Roles;
import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import com.quickmedicalcare.backend.registerLogin.SuperTokensUtilityClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Component
public class SuperTokensPrognosis implements SuperTokensPrognosisInterface {
    private SuperTokensUtilityClass superTokens;
    private static final String GET = "GET";
    private static final String userIdString = "userId";

    @Override
    public List<Roles> getUserRoles(String userId) throws IOException {
        String urlQuery = SuperTokensInterface
                .url_prefix + "/user/roles?userId=" + userId;
        HttpURLConnection con = superTokens.initAndSendRequest(new URL(urlQuery),
                Map.of("Content-Type", "application/json", "api_key", SuperTokensInterface
                                .api_key, "rid", SuperTokensInterface.rid_roles, "cdi-version",
                        SuperTokensInterface.cdi_version), GET, "");
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            JsonNode json = superTokens.obtainJsonNode(con);
            List<String> rolesString = ((new ObjectMapper()).convertValue(json.get("roles"), List.class));
            List<Roles> roles = new ArrayList<>();
            for (String role : rolesString) {
                roles.add(Roles.valueOf(role));
            }
            return roles;
        }
        return List.of();
    }
}
