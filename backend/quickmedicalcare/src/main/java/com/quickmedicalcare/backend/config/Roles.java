package com.quickmedicalcare.backend.config;

import com.quickmedicalcare.backend.prognosisPackage.PrognosisPayload;
import com.quickmedicalcare.backend.prognosisPackage.roleVisitor.RoleVisitor;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;

@Getter
public enum Roles {
    ROLE_ADMIN(5) {
        @Override
        public Map<String, Double> accept(RoleVisitor roleVisitor, PrognosisPayload payload) throws IOException {
            return roleVisitor.prognosisAsPremium(payload);
        }
    },
    ROLE_USER_BASIC(1) {
        @Override
        public Map<String, Double> accept(RoleVisitor roleVisitor, PrognosisPayload payload) throws IOException {
            return roleVisitor.prognosisAsBasic(payload);
        }
    },
    ROLE_USER_PREMIUM(2) {
        @Override
        public Map<String, Double> accept(RoleVisitor roleVisitor, PrognosisPayload payload) throws IOException {
            return roleVisitor.prognosisAsPremium(payload);
        }
    },
    ROLE_DOCTOR(3) {
        @Override
        public Map<String, Double> accept(RoleVisitor roleVisitor, PrognosisPayload payload) throws IOException {
            return roleVisitor.prognosisAsDoctor(payload);
        }
    };

    private final int priority;

    Roles(int priority) {
        this.priority = priority;
    }

    public static Roles fromString(String role) {
        for (Roles r : Roles.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }
        return null;
    }

    public abstract Map<String, Double> accept(RoleVisitor visitor, PrognosisPayload prognosisPayload) throws IOException;
}


