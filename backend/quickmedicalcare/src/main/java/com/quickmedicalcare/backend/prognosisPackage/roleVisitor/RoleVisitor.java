package com.quickmedicalcare.backend.prognosisPackage.roleVisitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.quickmedicalcare.backend.prognosisPackage.PrognosisPayload;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public interface RoleVisitor {
    String URL_NLP_REQUEST_BASIC = "http://127.0.0.1:5002/classify";
    String URL_NLP_REQUEST_PREMIUM = "http://127.0.0.1:5001/classify";
    String URL_NLP_REQUEST_DOCTOR = "http://127.0.0.1:5003/classify";
    String URL_ML_REQUEST_NON_MEDICAL_USERS = "http://127.0.0.1:5013/prognosis";
    String URL_ML_REQUEST_MEDICAL_USERS = "http://127.0.0.1:5014/prognosis";
    String GET = "GET";
    String POST = "POST";
    JsonNode prognosisAsBasic(PrognosisPayload prognosisPayload) throws IOException;
    JsonNode prognosisAsPremium(PrognosisPayload prognosisPayload) throws IOException;
    JsonNode prognosisAsDoctor(PrognosisPayload prognosisPayload, int age, String sex) throws IOException;

}
