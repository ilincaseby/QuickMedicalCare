package com.quickmedicalcare.backend.prognosisPackage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickmedicalcare.backend.config.Roles;
import com.quickmedicalcare.backend.correlationDataDatabase.services.UserDataCorrelationService;
import com.quickmedicalcare.backend.medicalDataDatabase.entities.UserPrivateData;
import com.quickmedicalcare.backend.medicalDataDatabase.services.UserPrivateDataService;
import com.quickmedicalcare.backend.prognosisPackage.roleVisitor.RoleVisitor;
import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import com.quickmedicalcare.backend.publicDataDatabase.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/prognosis")
public class PrognosisController {
    private final SuperTokensPrognosisInterface superTokensPrognosis;
    private final UserService userService;
    private final UserPrivateDataService userPrivateDataService;
    private final UserDataCorrelationService userPatientDataCorrelationService;
    private final RoleVisitor roleVisitor;
    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody PrognosisPayload prognosisPayload) throws IOException {
        List<Roles> userRoles = superTokensPrognosis.getUserRoles(SecurityContextHolder
                .getContext().getAuthentication().getName());
        User user = userService.findUserByUserIdSuperTokens(SecurityContextHolder
                .getContext().getAuthentication().getName());
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        long privateDataId = userPatientDataCorrelationService.getUserPrivateDataId(user.getId());
        if (privateDataId == 0) {
            return ResponseEntity.status(404).body("Private data not found");
        }
        UserPrivateData privateData = userPrivateDataService.getPatientById(privateDataId);
        if (privateData == null) {
            return ResponseEntity.status(404).body("Private data not found");
        }
        List<Roles> mutableRoles = new ArrayList<>(userRoles);
        mutableRoles.sort(Comparator.comparingInt(Roles::getPriority));
        Roles userRole = mutableRoles.get(userRoles.size() - 1);
        int age = privateData.getAge();
        if (prognosisPayload.getAge() != 0) {
            age = prognosisPayload.getAge();
        }
        String sex = privateData.getSex() == 1 ? "M" : "F";
        if (!prognosisPayload.getSex().equals("")) {
            sex = prognosisPayload.getSex();
        }
        JsonNode prognosisWProbs = userRole.accept(roleVisitor, prognosisPayload, age, sex);
        if (prognosisWProbs == null) {
            return ResponseEntity.status(500).body("Could not get any prognosis due to AI/ML servers malfunction");
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(prognosisWProbs);
        return ResponseEntity.status(200).body(node);
    }


}
