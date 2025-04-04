package com.quickmedicalcare.backend.prognosisPackage.roleVisitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.quickmedicalcare.backend.prognosisPackage.PrognosisPayload;
import com.quickmedicalcare.backend.prognosisPackage.SuperTokensPrognosisInterface;
import com.quickmedicalcare.backend.registerLogin.SuperTokensUtilityClass;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
@AllArgsConstructor
public class RoleVisitorImplementation implements RoleVisitor {
    private final SuperTokensPrognosisInterface superTokensPrognosis;
    private final SuperTokensUtilityClass httpInitializer;



    @Override
    public Map<String, Double> prognosisAsBasic(PrognosisPayload prognosisPayload) throws IOException {
        return getStringDoubleMap(prognosisPayload, URL_NLP_REQUEST_BASIC);
    }

    @Override
    public Map<String, Double> prognosisAsPremium(PrognosisPayload prognosisPayload) throws IOException {
        return getStringDoubleMap(prognosisPayload, URL_NLP_REQUEST_PREMIUM);
    }

    @Override
    public Map<String, Double> prognosisAsDoctor(PrognosisPayload prognosisPayload) {
        return Map.of();
    }

    private Map<String, Double> getStringDoubleMap(PrognosisPayload prognosisPayload, String URL_STRING) throws IOException {
        List<String> symptoms = getSymptoms(prognosisPayload, URL_STRING);
        if (symptoms.isEmpty()) {
            return Map.of();
        }
        JsonNode jsonPrognosis = getDiagnoses(symptoms, URL_NLP_REQUEST_BASIC);
        if (jsonPrognosis == null) {
            return Map.of();
        }
        Iterator<Map.Entry<String, JsonNode>> prognosisAsJson = jsonPrognosis.fields();
        Map<String, Double> prognosis = new HashMap<>();
        while (prognosisAsJson.hasNext()) {
            Map.Entry<String, JsonNode> entry = prognosisAsJson.next();
            prognosis.put(entry.getKey(), entry.getValue().asDouble());
        }
        return prognosis;
    }

    private List<String> getSymptoms(PrognosisPayload prognosisPayload, String URL_STRING) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", prognosisPayload.getMessage());
        HttpURLConnection con = httpInitializer
                .initAndSendRequest(new URL(URL_STRING),
                        Map.of("Content-Type", "application/json"),
                        POST, jsonObject.toString());
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return new ArrayList<>();
        }
        JsonNode jsonNode = httpInitializer.obtainJsonNode(con);
        JsonNode symptomsJSON = jsonNode.get("response");
        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < symptomsJSON.size(); i++) {
            symptoms.add(symptomsJSON.get(i).asText());
        }
        return symptoms;
    }

    private JsonNode getDiagnoses(List<String> symptoms, String URL_STRING) throws IOException {
        JSONObject json = new JSONObject();
        json.put("symptoms", symptoms);
        HttpURLConnection conPrognosis = httpInitializer
                .initAndSendRequest(new URL(URL_STRING),
                        Map.of("Content-Type", "application/json"), POST,
                        json.toString());
        if (conPrognosis.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        return httpInitializer.obtainJsonNode(conPrognosis);
    }
}
