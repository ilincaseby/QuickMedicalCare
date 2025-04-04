package com.quickmedicalcare.backend.config;

import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@Setter
@Getter
public class ContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String PUT = "PUT";
    private static final String url = "http://127.0.0.1:3567/appid-public/public/recipe/role";
    private static final String api_key = "f28fc40c8c5ef7be1a81dc61eabac15c";
    private static final String cdi_version = "5.1";
    private static final String rid_roles = "userroles";
    private static final BiFunction<String, List<String>, String> addRoleRequest = (a, b) -> {
        JSONObject aux = new JSONObject();
        aux.put("role", a);
        aux.put("permissions", b);
        return aux.toString();
    };

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        for (Roles role: Roles.values()) {
            try {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                while (status != HttpStatus.OK) {
                    status = addRole(role.toString(), new ArrayList<>());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HttpStatus addRole(String role, List<String> permissions) throws IOException {
        HttpURLConnection con = this.initAndSendRequest(new URL(url),
                Map.of("Content-Type", "application/json", "api_key", api_key, "rid", rid_roles, "cdi-version", cdi_version),
                PUT, addRoleRequest.apply(role, permissions));
        int status = con.getResponseCode();
        if (status == HttpStatus.OK.value()) {
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    public HttpURLConnection initAndSendRequest(URL url, Map<String, String> props, String method, String jsonInput) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        this.setRequestPropertyMethod(con, props, method);
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
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