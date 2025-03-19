package com.quickmedicalcare.backend.RegisterLogin;

import org.springframework.http.HttpStatusCode;

import java.io.IOException;

public interface SuperTokensInterface {
    String rid = "emailpassword";
    String rid_session = "session";
    String api_key = "f28fc40c8c5ef7be1a81dc61eabac15c";
    String cdi_version = "5.1";
    String url_prefix = "http://127.0.0.1:3567/appid-public/public/recipe";
    SuperTokensUtilityClass.RegisterAnswerClass register(String email, String password) throws IOException;
    SuperTokensUtilityClass.TokenCodeClass login(String email, String password) throws IOException;
    SuperTokensUtilityClass.TokenClass getToken(String userId) throws IOException;
    String obtainAccess(String token, String antiCsrfToken) throws IOException;
}
