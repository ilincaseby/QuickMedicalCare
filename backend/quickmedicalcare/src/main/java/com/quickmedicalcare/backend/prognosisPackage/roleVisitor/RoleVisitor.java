package com.quickmedicalcare.backend.prognosisPackage.roleVisitor;

import com.quickmedicalcare.backend.prognosisPackage.PrognosisPayload;

import java.io.IOException;
import java.util.Map;

public interface RoleVisitor {
    String URL_NLP_REQUEST_BASIC = "http://127.0.0.1:5002/classify";
    String URL_NLP_REQUEST_PREMIUM = "http://127.0.0.1:5001/classify";
    String URL_ML_REQUEST_BASIC = "http://127.0.0.1:5005/prognosis";
    String GET = "GET";
    String POST = "POST";
    Map<String, Double> prognosisAsBasic(PrognosisPayload prognosisPayload) throws IOException;
    Map<String, Double> prognosisAsPremium(PrognosisPayload prognosisPayload) throws IOException;
    Map<String, Double> prognosisAsDoctor(PrognosisPayload prognosisPayload);

}
