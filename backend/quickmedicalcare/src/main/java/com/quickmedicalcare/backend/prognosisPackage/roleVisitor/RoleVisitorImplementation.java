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
    private final SuperTokensUtilityClass httpInitializer;


    @Override
    public JsonNode prognosisAsBasic(PrognosisPayload prognosisPayload) throws IOException {
        return getDiagnosis(prognosisPayload, URL_NLP_REQUEST_BASIC, URL_ML_REQUEST_NON_MEDICAL_USERS, -1, "M");
        //return getStringDoubleMap(prognosisPayload, URL_NLP_REQUEST_BASIC);
    }

    @Override
    public JsonNode prognosisAsPremium(PrognosisPayload prognosisPayload) throws IOException {
        return getDiagnosis(prognosisPayload, URL_NLP_REQUEST_PREMIUM, URL_ML_REQUEST_NON_MEDICAL_USERS, -1, "M");
        //return getStringDoubleMap(prognosisPayload, URL_NLP_REQUEST_PREMIUM);
    }

    @Override
    public JsonNode prognosisAsDoctor(PrognosisPayload prognosisPayload, int age, String sex) throws IOException {
        return getDiagnosis(prognosisPayload, URL_NLP_REQUEST_DOCTOR, URL_ML_REQUEST_MEDICAL_USERS, age, sex);
        //return Map.of();
    }

    private JsonNode getDiagnosis(PrognosisPayload prognosisPayload, String URL_STRING_SYMPTOMS, String URL_STRING_DIAGNOSIS, int age, String sex) throws IOException {
        List<String> symptoms = getSymptoms(prognosisPayload, URL_STRING_SYMPTOMS);
        System.out.println(symptoms);
        if (symptoms.isEmpty()) {
            return null;
        }

        JsonNode jsonPrognosis = getDiagnoses(symptoms, URL_STRING_DIAGNOSIS, age, sex);
        if (jsonPrognosis == null) {
            return null;
        }
        return jsonPrognosis;
    }

    private List<String> getSymptoms(PrognosisPayload prognosisPayload, String URL_STRING_SYMPTOMS) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", prognosisPayload.getMessage());
        HttpURLConnection con = httpInitializer
                .initAndSendRequest(new URL(URL_STRING_SYMPTOMS),
                        Map.of("Content-Type", "application/json"),
                        POST, jsonObject.toString());
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return new ArrayList<>();
        }
        JsonNode jsonNode = httpInitializer.obtainJsonNode(con);
        System.out.println("bau");
        System.out.println(jsonNode);
        JsonNode symptomsJSON = jsonNode.get("response");
        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < symptomsJSON.size(); i++) {
            symptoms.add(symptomsJSON.get(i).asText());
        }
        return symptoms;
    }

    private JsonNode getDiagnoses(List<String> symptoms, String URL_STRING_DIAGNOSIS, int age, String sex) throws IOException {
        JSONObject json = new JSONObject();
        json.put("symptoms", symptoms);
        json.put("AGE", age);
        json.put("SEX", sex);
        System.out.println(json);
        HttpURLConnection conPrognosis = httpInitializer
                .initAndSendRequest(new URL(URL_STRING_DIAGNOSIS),
                        Map.of("Content-Type", "application/json"), POST,
                        json.toString());
        System.out.println(conPrognosis.getResponseCode());
        if (conPrognosis.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        JsonNode jsonNode = httpInitializer.obtainJsonNode(conPrognosis);
        System.out.println(jsonNode);
        return jsonNode;
    }
}
