package com.quickmedicalcare.backend.prognosisPackage;

import com.quickmedicalcare.backend.config.Roles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface SuperTokensPrognosisInterface {
    List<Roles> getUserRoles(String userId) throws IOException;
}
