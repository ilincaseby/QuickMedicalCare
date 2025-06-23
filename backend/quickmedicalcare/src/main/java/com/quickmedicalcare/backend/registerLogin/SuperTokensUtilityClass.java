package com.quickmedicalcare.backend.registerLogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class SuperTokensUtilityClass implements SuperTokensInterface {

    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private static final BiFunction<String, String, String> getInputStringEmailPass = (e, p) ->
            "{\"email\":\"" + e + "\",\"password\":\"" + p + "\"}";
    private static final Function<String, String> getInputUserId = a -> "{\"userId\": " + a +
            ",\"userDataInJWT\": {\"test\": 123},\"userDataInDatabase\": {\"test\": 123}," +
            "\"enableAntiCsrf\": false,\"useDynamicSigningKey\": false}";
    private static final BiFunction<String, String, String> getInputTokens = (a, b) -> {
        JSONObject aux = new JSONObject();
        aux.put("accessToken", a);
        aux.put("enableAntiCsrf", false);
        aux.put("doAntiCsrfCheck", false);
        aux.put("checkDatabase", false);
        aux.put("antiCsrfToken", b);
        return aux.toString();
    };
    private static final BiFunction<String, String, String> getRoleSet = (a, b) -> {
        JSONObject aux = new JSONObject();
        aux.put("userId", a);
        aux.put("role", b);
        return aux.toString();
    };

    public static class TokenCodeClass {
        private int code;
        private String user_id;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public TokenCodeClass(int code, String user_id) {
            this.code = code;
            this.user_id = user_id;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TokenClass {
        private String accessToken;
        private String refreshToken;
        private String antiCsrfToken;
        private Long expiresIn;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RegisterAnswerClass{
        private HttpStatusCode statusCode;
        private String userId;
    }
    @Override
    public RegisterAnswerClass register(String email, String password) {
        try {
            HttpURLConnection con = this.initAndSendRequest(new URL(url_prefix.concat("/signup")),
                    Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid, "cdi-version", cdi_version),
                    POST, getInputStringEmailPass.apply(email, password));
            int status = con.getResponseCode();
            if (status == HttpStatus.OK.value()) {
                return new RegisterAnswerClass(HttpStatusCode.valueOf(201), obtainUserId(con));
            }
            return new RegisterAnswerClass(HttpStatusCode.valueOf(status), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterAnswerClass(HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public TokenCodeClass login(String email, String password) {
        try {
            HttpURLConnection con = this.initAndSendRequest(new URL(url_prefix.concat("/signin")),
                    Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid, "cdi-version", cdi_version),
                    POST, getInputStringEmailPass.apply(email, password));
            int status = con.getResponseCode();
            if (status == HttpStatus.OK.value()) {
                String userId = obtainUserId(con);
                if (userId != null) {
                    return new TokenCodeClass(status, userId);
                }
            }
            return new TokenCodeClass(400, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new TokenCodeClass(HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    @Override
    public TokenClass getToken(String userId) throws IOException {
        HttpURLConnection con = this.initAndSendRequest(new URL(url_prefix.concat("/session")),
                Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid_session, "cdi-version", cdi_version),
                POST, getInputUserId.apply(userId));
        int status = con.getResponseCode();
        if (status == HttpStatus.OK.value()) {
            return obtainTokens(con);
        }
        return new TokenClass(null, null, null, Integer.toUnsignedLong(0));
    }

    @Override
    public String obtainAccess(String token, String antiCsrfToken) throws IOException {
        HttpURLConnection con = this.initAndSendRequest(new URL(url_prefix.concat("/session/verify")),
                Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid_session, "cdi-version", cdi_version),
                POST, getInputTokens.apply(token, antiCsrfToken));
        System.out.println(getInputTokens.apply(token, antiCsrfToken));
        JsonNode jsonNode = obtainJsonNode(con);
        System.out.println(jsonNode);
        System.out.println(con.getResponseCode());

        System.out.println(jsonNode);
        if (con.getResponseCode() == HttpStatus.OK.value()) {
            if (jsonNode.get("status").asText().equals("OK")) {
                return jsonNode.get("session").get("userId").asText();
            }
        }
        return "";
    }

    @Override
    public HttpStatus setRole(String userId, String role) throws IOException {
        HttpURLConnection con = this.initAndSendRequest(new URL(url_prefix.concat("/user/role")),
                Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid_roles, "cdi-version", cdi_version),
                PUT, getRoleSet.apply(userId, role));
        int status = con.getResponseCode();
        if (status == HttpStatus.OK.value()) {
            JsonNode jsonNode = obtainJsonNode(con);
            if (jsonNode.get("didUserAlreadyHaveRole").asBoolean()) {
                return HttpStatus.CONFLICT;
            }
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }


    private String obtainUserId(HttpURLConnection con) throws IOException {
        JsonNode jsonNode = obtainJsonNode(con);
        if (jsonNode.get("status").asText().equals("OK")) {
            if (jsonNode.get("user") != null) {
                return jsonNode.get("user").get("id").asText();
            }
        }
        return null;
    }

    private TokenClass obtainTokens(HttpURLConnection con) throws IOException {
        JsonNode jsonNode = obtainJsonNode(con);
        if (jsonNode.get("status").asText().equals("OK")) {
            String antiCsrfToken = jsonNode.has("antiCsrfToken") && !jsonNode.get("antiCsrfToken").isNull()
                    ? jsonNode.get("antiCsrfToken").asText()
                    : null;
            return new TokenClass(jsonNode.get("accessToken").get("token").asText(),
                    jsonNode.get("refreshToken").get("token").asText(),
                    antiCsrfToken,
                    jsonNode.get("accessToken").get("expiry").asLong() -
                            jsonNode.get("accessToken").get("createdTime").asLong());
        }
        return new TokenClass(null, null, null, Integer.toUnsignedLong(0));
    }

    public JsonNode obtainJsonNode(HttpURLConnection con) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        br.close();
        String json = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        return jsonNode;
    }

    public HttpURLConnection initAndSendRequest(URL url, Map<String, String> props, String method, String jsonInput) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        this.setRequestPropertyMethod(con, props, method);
        con.setDoOutput(true);
        if (!"GET".equalsIgnoreCase(method)) {
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        return con;
    }

    private void setRequestPropertyMethod(HttpURLConnection con, Map<String, String> requestProperties, String method) throws ProtocolException {
        for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        con.setRequestMethod(method);
    }
}
