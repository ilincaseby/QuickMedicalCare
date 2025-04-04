package com.quickmedicalcare.backend.prognosisPackage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickmedicalcare.backend.config.Roles;
import com.quickmedicalcare.backend.prognosisPackage.roleVisitor.RoleVisitor;
import com.quickmedicalcare.backend.registerLogin.SuperTokensUtilityClass;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/prognosis")
public class PrognosisController {
    private final SuperTokensPrognosisInterface superTokensPrognosis;
    private final RoleVisitor roleVisitor;
    @PostMapping("/predict")
    public ResponseEntity<String> predict(@RequestBody PrognosisPayload prognosisPayload) throws IOException {
        List<Roles> userRoles = superTokensPrognosis.getUserRoles(SecurityContextHolder
                .getContext().getAuthentication().getName());
        List<Roles> mutableRoles = new ArrayList<>(userRoles);
        mutableRoles.sort(Comparator.comparingInt(Roles::getPriority));
        Roles userRole = mutableRoles.get(userRoles.size() - 1);
        Map<String, Double> prognosisWProbs = userRole.accept(roleVisitor, prognosisPayload);
        if (prognosisWProbs.isEmpty()) {
            return ResponseEntity.status(400).body("Could not get any prognosis due to microservices malfunction");
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(prognosisWProbs);
        return ResponseEntity.status(200).body(mapper.writeValueAsString(node));
    }


}
