package com.quickmedicalcare.backend.changeInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import com.quickmedicalcare.backend.registerLogin.SuperTokensUtilityClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;

@Component
@Getter
@Setter
@AllArgsConstructor
public class SuperTokensResetPasswordUtilityClass implements SuperTokensResetPasswordInterface{
    private SuperTokensUtilityClass auxSuperTokensAPI;
    private static BiFunction<String, String, String> JSONTokenRequest = (a, b) -> "{\n" +
            "  \"userId\": " + a +",\n" +
            "  \"email\": " + b + "\n" +
            "}";
    private static BiFunction<String, String, String> JSONResetRequest = (a, b) -> "{\n" +
            "  \"method\": \"token\",\n" +
            "  \"token\": " + a +",\n" +
            "  \"newPassword\": " + b + "\n" +
            "}";

    private static final String POST = "POST";
    private static final String PUT = "PUT";

    @Override
    public String getPasswordToken(String userId, String email) throws IOException {
        HttpURLConnection con = auxSuperTokensAPI.initAndSendRequest(new URL(this.url_prefix
                .concat("/user/password/reset/token")), Map.of("Content-Type", "application/json",
                        "api_key", api_key, "rid", rid, "cdi-version", cdi_version), POST,
                JSONTokenRequest.apply(userId, email));
        int status = con.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            JsonNode response = auxSuperTokensAPI.obtainJsonNode(con);
            if (response.get("status").asText().equals("OK")) {
                return response.get("token").asText();
            }
        }
        return null;
    }

    @Override
    public Boolean resetPassword(String token, String newPassword) throws IOException{
        HttpURLConnection con = auxSuperTokensAPI.initAndSendRequest(new URL(this.url_prefix
                        .concat("/user/password/reset")), Map.of("Content-Type", "application/json",
                        "api_key", api_key, "rid", rid, "cdi-version", cdi_version), POST,
                JSONResetRequest.apply(token, newPassword));
        int status = con.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            JsonNode response = auxSuperTokensAPI.obtainJsonNode(con);
            if (response.get("status").asText().equals("OK")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean resetEmail(String email, String userId, String password) throws IOException {
        HttpURLConnection con = auxSuperTokensAPI.initAndSendRequest(new URL(url_prefix.concat("/user")),
                Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid, "cdi-version", cdi_version),
                PUT, JSONResetEmailRequest(userId, email, password));
        int status = con.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            JsonNode response = auxSuperTokensAPI.obtainJsonNode(con);
            if (response.get("status").asText().equals("OK")) {
                return true;
            }
        }
        return false;
    }

    private String JSONResetEmailRequest(String userId, String email, String password) throws IOException {
        JSONObject json = new JSONObject();
        json.put("recipeUserId", userId);
        json.put("email", email);
        json.put("password", password);
        return json.toString();
    }
}
